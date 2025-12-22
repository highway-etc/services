package com.highway.etc.repository;

import com.highway.etc.api.dto.RevenuePoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RevenueRepository {

    private static final int MIN_WINDOW_MINUTES = 30;
    private static final int MAX_WINDOW_MINUTES = 24 * 60;
    private static final int DEFAULT_WINDOW_MINUTES = 240;

    private final JdbcTemplate jdbcTemplate;

    public RevenueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RevenuePoint> forecast(Integer stationId, Integer windowMinutes) {
        int window = normalizeWindow(windowMinutes);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusMinutes(window);

        StringBuilder sql = new StringBuilder(
                "SELECT station_id, MAX(kkmc) AS kkmc, "
                + "CAST(DATE_FORMAT(gcsj,'%Y-%m-%d %H:%i:00') AS DATETIME) AS window_start, "
                + "CAST(DATE_ADD(DATE_FORMAT(gcsj,'%Y-%m-%d %H:%i:00'), INTERVAL 30 SECOND) AS DATETIME) AS window_end, "
                + "SUM(CASE WHEN hpzl IN ('02') THEN 1 ELSE 0 END) AS cnt_small, "
                + "SUM(CASE WHEN hpzl IN ('01') THEN 1 ELSE 0 END) AS cnt_large, "
                + "SUM(CASE WHEN hpzl IN ('52','NEV-S') THEN 1 ELSE 0 END) AS cnt_nev_small, "
                + "SUM(CASE WHEN hpzl IN ('53','NEV-L') THEN 1 ELSE 0 END) AS cnt_nev_large, "
                + "SUM(CASE WHEN hpzl IN ('21') THEN 1 ELSE 0 END) AS cnt_taxi, "
                + "COUNT(*) AS cnt_total "
                + "FROM traffic_pass_dev WHERE gcsj >= ?");
        List<Object> params = new ArrayList<>();
        params.add(Timestamp.valueOf(since));
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        sql.append(" GROUP BY station_id, window_start, window_end ORDER BY window_start DESC LIMIT 200");

        List<RevenuePoint> rows = jdbcTemplate.query(sql.toString(), params.toArray(), mapper());
        applyForecast(rows);
        return rows;
    }

    private int normalizeWindow(Integer windowMinutes) {
        if (windowMinutes == null) {
            return DEFAULT_WINDOW_MINUTES;
        }
        return Math.min(MAX_WINDOW_MINUTES, Math.max(MIN_WINDOW_MINUTES, windowMinutes));
    }

    private RowMapper<RevenuePoint> mapper() {
        return new RowMapper<>() {
            @Override
            public RevenuePoint mapRow(ResultSet rs, int rowNum) throws SQLException {
                RevenuePoint r = new RevenuePoint();
                r.setStationId((Integer) rs.getObject("station_id"));
                r.setStationName(rs.getString("kkmc"));
                Timestamp ws = rs.getTimestamp("window_start");
                Timestamp we = rs.getTimestamp("window_end");
                if (ws != null) {
                    r.setWindowStart(ws.toLocalDateTime());
                }
                if (we != null) {
                    r.setWindowEnd(we.toLocalDateTime());
                }

                Map<String, Long> bucket = new HashMap<>();
                bucket.put("small", rs.getLong("cnt_small"));
                bucket.put("large", rs.getLong("cnt_large"));
                bucket.put("nev_small", rs.getLong("cnt_nev_small"));
                bucket.put("nev_large", rs.getLong("cnt_nev_large"));
                bucket.put("taxi", rs.getLong("cnt_taxi"));

                double revenue = computeRevenue(bucket);
                r.setTrafficCount(rs.getLong("cnt_total"));
                r.setVehicleType("综合");
                r.setRevenue(round(revenue));
                // forecast will be filled later
                return r;
            }
        };
    }

    private double computeRevenue(Map<String, Long> bucket) {
        double total = 0.0;
        total += bucket.getOrDefault("small", 0L) * 0.8;   // 轿车
        total += bucket.getOrDefault("large", 0L) * 1.8;   // 大型车
        total += bucket.getOrDefault("nev_small", 0L) * 0.6; // 新能源优惠
        total += bucket.getOrDefault("nev_large", 0L) * 1.4;
        total += bucket.getOrDefault("taxi", 0L) * 0.9;   // 出租客运
        return total;
    }

    private void applyForecast(List<RevenuePoint> rows) {
        double moving = 0;
        int n = 0;
        for (int i = rows.size() - 1; i >= 0; i--) {
            RevenuePoint r = rows.get(i);
            moving = moving * 0.7 + (r.getRevenue() == null ? 0 : r.getRevenue()) * 0.3;
            n++;
            double baseline = n >= 4 ? moving : (r.getRevenue() == null ? 0 : r.getRevenue());
            r.setForecastRevenue(round(baseline * 1.05));
        }
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
