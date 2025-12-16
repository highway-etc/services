package com.highway.etc.repository;

import com.highway.etc.api.dto.AlertResponse;
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

    public List<AlertResponse> query(String plateMask, String start, String end) {
        StringBuilder sql = new StringBuilder("SELECT hphm_mask,first_station_id,created_at FROM alert_plate_clone WHERE 1=1");
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

    private final RowMapper<AlertResponse> mapper = new RowMapper<>() {
        @Override
        public AlertResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            AlertResponse a = new AlertResponse();
            a.setStationId((Integer) rs.getObject("first_station_id"));
            a.setLicensePlate(rs.getString("hphm_mask"));
            a.setTimestamp(rs.getTimestamp("created_at").toLocalDateTime());
            a.setAlertType("Plate Clone");
            return a;
        }
    };
}
