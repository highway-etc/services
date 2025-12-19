package com.highway.etc.repository;

import com.highway.etc.api.dto.OverviewResponse;
import com.highway.etc.api.dto.TopStation;
import com.highway.etc.api.dto.TrendPoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DashboardRepository {

    private static final int MIN_WINDOW_MINUTES = 5;
    private static final int MAX_WINDOW_MINUTES = 24 * 60;
    private static final int DEFAULT_WINDOW_MINUTES = 24 * 60;
    private static final int TOP_LIMIT = 5;

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public OverviewResponse overview(Integer windowMinutes) {
        int window = normalizeWindow(windowMinutes);
        // MyCAT 对 MAX(timestamp) 聚合存在兼容性问题，改为按时间倒序取最新一条
        Timestamp anchor = jdbcTemplate.queryForObject(
                "SELECT gcsj FROM traffic_pass_dev ORDER BY gcsj DESC LIMIT 1",
                Timestamp.class);
        if (anchor == null) {
            return emptyResponse();
        }
        Timestamp since = Timestamp.valueOf(anchor.toLocalDateTime().minusMinutes(window));

        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM traffic_pass_dev WHERE gcsj >= ?",
                new Object[]{since}, Long.class);

        Long uniquePlates = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT hphm_mask) FROM traffic_pass_dev WHERE gcsj >= ?",
                new Object[]{since}, Long.class);

        Long alerts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alert_plate_clone WHERE created_at >= ?",
                new Object[]{since}, Long.class);

        List<TopStation> topStations = jdbcTemplate.query(
                "SELECT station_id, COUNT(*) AS cnt FROM traffic_pass_dev WHERE gcsj >= ? "
                + "GROUP BY station_id ORDER BY cnt DESC LIMIT ?",
                new Object[]{since, TOP_LIMIT}, topMapper);

        List<TrendPoint> trend = jdbcTemplate.query(
                "SELECT CAST(DATE_FORMAT(gcsj,'%Y-%m-%d %H:%i:00') AS DATETIME) AS window_start, "
                + "COUNT(*) AS cnt FROM traffic_pass_dev WHERE gcsj >= ? "
                + "GROUP BY window_start ORDER BY window_start",
                new Object[]{since}, trendMapper);

        OverviewResponse resp = new OverviewResponse();
        resp.setTotalTraffic(total == null ? 0 : total);
        resp.setUniquePlates(uniquePlates);
        resp.setAlertCount(alerts == null ? 0 : alerts);
        resp.setTopStations(topStations);
        resp.setTrafficTrend(trend);
        return resp;
    }

    private OverviewResponse emptyResponse() {
        OverviewResponse resp = new OverviewResponse();
        resp.setTotalTraffic(0);
        resp.setUniquePlates(0L);
        resp.setAlertCount(0);
        resp.setTopStations(java.util.Collections.emptyList());
        resp.setTrafficTrend(java.util.Collections.emptyList());
        return resp;
    }

    private int normalizeWindow(Integer windowMinutes) {
        if (windowMinutes == null) {
            return DEFAULT_WINDOW_MINUTES;
        }
        return Math.min(MAX_WINDOW_MINUTES, Math.max(MIN_WINDOW_MINUTES, windowMinutes));
    }

    private final RowMapper<TopStation> topMapper = new RowMapper<>() {
        @Override
        public TopStation mapRow(ResultSet rs, int rowNum) throws SQLException {
            TopStation t = new TopStation();
            t.setStationId((Integer) rs.getObject("station_id"));
            t.setCount(rs.getLong("cnt"));
            return t;
        }
    };

    private final RowMapper<TrendPoint> trendMapper = new RowMapper<>() {
        @Override
        public TrendPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrendPoint p = new TrendPoint();
            p.setWindowStart(rs.getTimestamp("window_start").toLocalDateTime());
            p.setCount(rs.getLong("cnt"));
            return p;
        }
    };
}
