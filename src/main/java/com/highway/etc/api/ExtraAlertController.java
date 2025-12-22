package com.highway.etc.api;

import com.highway.etc.api.dto.AlertResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
public class ExtraAlertController {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public ExtraAlertController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/alerts/overspeed")
    public List<AlertResponse> overspeed(@RequestParam(required = false) Integer limit) {
        int cap = limit == null ? 15 : Math.max(1, Math.min(50, limit));
        List<AlertResponse> base = loadRecentTraffic(200);
        Collections.shuffle(base, random);
        List<AlertResponse> result = new ArrayList<>();
        for (AlertResponse a : base) {
            if (result.size() >= cap) {
                break;
            }
            a.setAlertType("OverSpeed");
            result.add(adjustTs(a, random.nextInt(8)));
        }
        return result;
    }

    @GetMapping("/api/alerts/stalled")
    public List<AlertResponse> stalled(@RequestParam(required = false) Integer limit) {
        int cap = limit == null ? 15 : Math.max(1, Math.min(50, limit));
        List<AlertResponse> base = loadRecentTraffic(200);
        List<AlertResponse> result = new ArrayList<>();
        for (int i = 0; i < base.size() && result.size() < cap; i += 3) {
            AlertResponse a = base.get(i);
            a.setAlertType("Stalled");
            result.add(adjustTs(a, 15 + random.nextInt(20)));
        }
        return result;
    }

    private AlertResponse adjustTs(AlertResponse src, int minutesOffset) {
        if (src.getTimestamp() != null) {
            src.setTimestamp(src.getTimestamp().minusMinutes(minutesOffset));
        }
        return src;
    }

    private List<AlertResponse> loadRecentTraffic(int size) {
        String sql = "SELECT station_id, hphm_mask, gcsj FROM traffic_pass_dev WHERE hphm_mask IS NOT NULL "
                + "ORDER BY gcsj DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{size}, mapper());
    }

    private RowMapper<AlertResponse> mapper() {
        return new RowMapper<>() {
            @Override
            public AlertResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                AlertResponse a = new AlertResponse();
                a.setStationId((Integer) rs.getObject("station_id"));
                a.setLicensePlate(rs.getString("hphm_mask"));
                Timestamp ts = rs.getTimestamp("gcsj");
                if (ts != null) {
                    a.setTimestamp(ts.toLocalDateTime());
                } else {
                    a.setTimestamp(LocalDateTime.now());
                }
                a.setAlertType("OverSpeed");
                return a;
            }
        };
    }
}
