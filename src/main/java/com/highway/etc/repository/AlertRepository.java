package com.highway.etc.repository;

import com.highway.etc.model.AlertRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AlertRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AlertRecord> query(String plateMask, String start, String end) {
        StringBuilder sql = new StringBuilder("SELECT alert_id,hphm_mask,first_station_id,second_station_id,time_gap_sec,distance_km,speed_kmh,confidence,created_at FROM alert_plate_clone WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (plateMask != null && !plateMask.isBlank()) {
            sql.append(" AND hphm_mask LIKE ?");
            params.add("%" + plateMask + "%");
        }
        if (start != null && !start.isBlank()) {
            sql.append(" AND created_at >= ?");
            params.add(start);
        }
        if (end != null && !end.isBlank()) {
            sql.append(" AND created_at <= ?");
            params.add(end);
        }
        sql.append(" ORDER BY created_at DESC LIMIT 200");
        return jdbcTemplate.query(sql.toString(), params.toArray(), mapper);
    }

    private final RowMapper<AlertRecord> mapper = new RowMapper<>() {
        @Override
        public AlertRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            AlertRecord a = new AlertRecord();
            a.setAlertId(rs.getLong("alert_id"));
            a.setHphmMask(rs.getString("hphm_mask"));
            a.setFirstStationId((Integer) rs.getObject("first_station_id"));
            a.setSecondStationId((Integer) rs.getObject("second_station_id"));
            a.setTimeGapSec(rs.getLong("time_gap_sec"));
            a.setDistanceKm((Double) rs.getObject("distance_km"));
            a.setSpeedKmh((Double) rs.getObject("speed_kmh"));
            a.setConfidence((Double) rs.getObject("confidence"));
            a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return a;
        }
    };
}
