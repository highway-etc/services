package com.highway.etc.repository;

import com.highway.etc.api.dto.CongestionPoint;
import com.highway.etc.api.dto.DeviceHealthResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CongestionRepository {

    private static final int MIN_WINDOW_MINUTES = 5;
    private static final int MAX_WINDOW_MINUTES = 180;
    private static final int DEFAULT_WINDOW_MINUTES = 60;
    private static final double CAPACITY_PER_MINUTE = 120.0; // 7200 pcu/h 近似

    private final JdbcTemplate jdbcTemplate;

    public CongestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CongestionPoint> query(Integer windowMinutes, Integer stationId) {
        int window = normalizeWindow(windowMinutes);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusMinutes(window);

        StringBuilder sql = new StringBuilder(
                "SELECT station_id, MAX(kkmc) AS kkmc, COUNT(*) AS cnt, "
                + "MIN(gcsj) AS start_ts, MAX(gcsj) AS end_ts "
                + "FROM traffic_pass_dev WHERE gcsj >= ?");
        List<Object> params = new ArrayList<>();
        params.add(Timestamp.valueOf(since));
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        sql.append(" GROUP BY station_id ORDER BY cnt DESC LIMIT 50");

        List<CongestionPoint> list = jdbcTemplate.query(sql.toString(), params.toArray(), mapper(now, window));
        return list;
    }

    public List<DeviceHealthResponse> health(Integer stationId) {
        StringBuilder sql = new StringBuilder(
                "SELECT station_id, MAX(kkmc) AS kkmc, MAX(gcsj) AS last_ts, COUNT(*) AS cnt "
                + "FROM traffic_pass_dev WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        sql.append(" GROUP BY station_id ORDER BY last_ts DESC LIMIT 50");

        LocalDateTime now = LocalDateTime.now();
        return jdbcTemplate.query(sql.toString(), params.toArray(), healthMapper(now));
    }

    private int normalizeWindow(Integer windowMinutes) {
        if (windowMinutes == null) {
            return DEFAULT_WINDOW_MINUTES;
        }
        return Math.min(MAX_WINDOW_MINUTES, Math.max(MIN_WINDOW_MINUTES, windowMinutes));
    }

    private RowMapper<CongestionPoint> mapper(LocalDateTime now, int windowMinutes) {
        return new RowMapper<>() {
            @Override
            public CongestionPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
                CongestionPoint c = new CongestionPoint();
                c.setStationId((Integer) rs.getObject("station_id"));
                c.setStationName(rs.getString("kkmc"));

                Timestamp startTs = rs.getTimestamp("start_ts");
                Timestamp endTs = rs.getTimestamp("end_ts");
                if (startTs != null) {
                    c.setWindowStart(startTs.toLocalDateTime());
                }
                if (endTs != null) {
                    c.setWindowEnd(endTs.toLocalDateTime());
                }

                long cnt = rs.getLong("cnt");
                double flowPerMin = windowMinutes <= 0 ? 0 : (double) cnt / windowMinutes;
                double cci = Math.min(5.0, (flowPerMin / CAPACITY_PER_MINUTE) * 5.0);

                c.setFlowPerMinute(round(flowPerMin));
                c.setCongestionIndex(round(cci));
                c.setLevel(level(cci));
                c.setAvgSpeedKmh(round(estimateSpeed(cci)));
                c.setOccupancy(round(estimateOccupancy(cci)));

                LocalDateTime last = endTs == null ? now : endTs.toLocalDateTime();
                double health = computeHealth(now, last);
                c.setHealthScore(round(health * 100));
                return c;
            }
        };
    }

    private RowMapper<DeviceHealthResponse> healthMapper(LocalDateTime now) {
        return new RowMapper<>() {
            @Override
            public DeviceHealthResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceHealthResponse h = new DeviceHealthResponse();
                h.setStationId((Integer) rs.getObject("station_id"));
                h.setStationName(rs.getString("kkmc"));
                Timestamp ts = rs.getTimestamp("last_ts");
                LocalDateTime last = ts == null ? now.minusHours(2) : ts.toLocalDateTime();
                h.setLastHeartbeat(last);

                double minutesGap = Duration.between(last, now).toMinutes();
                double uptime = Math.max(0.0, Math.min(1.0, 1.0 - minutesGap / 60.0));
                // 站点级轻量错误率，基于 stationId 生成稳定小数，避免完全随机
                int sid = h.getStationId() == null ? 1 : h.getStationId();
                double errorRate = 0.001 + (sid % 7) * 0.0005;

                h.setUptimePct(round(uptime * 100));
                h.setErrorRate(round(errorRate * 100));
                boolean maint = minutesGap > 90;
                h.setMaintenanceFlag(maint);
                h.setStatus(maint ? "维护/离线" : (uptime > 0.9 ? "正常" : "波动"));
                return h;
            }
        };
    }

    private String level(double cci) {
        if (cci < 1.5) {
            return "畅通";
        }
        if (cci < 2.5) {
            return "缓行";
        }
        if (cci < 3.5) {
            return "拥堵";
        }
        return "严重拥堵";
    }

    private double estimateSpeed(double cci) {
        // 粗略映射：CCI 越高速度越低，最低 15km/h
        double speed = 100 - cci * 18;
        return Math.max(15, speed);
    }

    private double estimateOccupancy(double cci) {
        // 占有率 10%-95% 区间
        return Math.min(0.95, Math.max(0.1, 0.18 * cci + 0.1));
    }

    private double computeHealth(LocalDateTime now, LocalDateTime last) {
        long minutes = Duration.between(last, now).toMinutes();
        if (minutes <= 5) {
            return 1.0;
        }
        if (minutes >= 60) {
            return 0.1;
        }
        double factor = 1.0 - (minutes - 5) / 55.0;
        return Math.max(0.1, factor);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
