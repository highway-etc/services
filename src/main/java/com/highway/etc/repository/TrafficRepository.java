package com.highway.etc.repository;

import com.highway.etc.api.dto.TrafficPageResponse;
import com.highway.etc.api.dto.TrafficResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TrafficRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrafficRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TrafficPageResponse query(Integer stationId, String start, String end, String licensePlate, int page, int size) {
        int safeSize = Math.max(size, 1);
        int offset = Math.max(page - 1, 0) * safeSize;
        StringBuilder base = new StringBuilder(" FROM traffic_pass_dev WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (stationId != null) {
            base.append(" AND station_id = ?");
            params.add(stationId);
        }
        if (start != null && !start.isBlank()) {
            base.append(" AND gcsj >= ?");
            params.add(start);
        }
        if (end != null && !end.isBlank()) {
            base.append(" AND gcsj <= ?");
            params.add(end);
        }
        if (licensePlate != null && !licensePlate.isBlank()) {
            base.append(" AND hphm_mask LIKE ?");
            params.add("%" + licensePlate.trim() + "%");
        }

        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*)" + base, params.toArray(), Long.class);

        StringBuilder sql = new StringBuilder(
                "SELECT gcsj AS timestamp,hphm_mask AS license_plate,station_id,NULL AS speed" + base
                + " ORDER BY gcsj DESC LIMIT ? OFFSET ?");
        params.add(safeSize);
        params.add(offset);
        List<TrafficResponse> records = jdbcTemplate.query(sql.toString(), params.toArray(), mapper);

        TrafficPageResponse resp = new TrafficPageResponse();
        resp.setTotal(total == null ? 0 : total);
        resp.setRecords(records);
        return resp;
    }

    private final RowMapper<TrafficResponse> mapper = new RowMapper<>() {
        @Override
        public TrafficResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrafficResponse t = new TrafficResponse();
            t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            t.setLicensePlate(rs.getString("license_plate"));
            t.setStationId((Integer) rs.getObject("station_id"));
            t.setSpeed((Double) rs.getObject("speed"));
            return t;
        }
    };
}
