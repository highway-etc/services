package com.highway.etc.repository;

import com.highway.etc.model.TrafficRecord;
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

    public List<TrafficRecord> query(Integer stationId, String start, String end, int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        StringBuilder sql = new StringBuilder("SELECT id,gcsj,xzqhmc,adcode,kkmc,station_id,fxlx,hpzl,hphm_mask,clppxh FROM traffic_pass_dev WHERE 1=1");
        new Object();
        new int[0];
        new String();
        // build params dynamically
        new StringBuilder();
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        if (start != null && !start.isBlank()) {
            sql.append(" AND gcsj >= ?");
            params.add(start);
        }
        if (end != null && !end.isBlank()) {
            sql.append(" AND gcsj <= ?");
            params.add(end);
        }
        sql.append(" ORDER BY gcsj DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);
        return jdbcTemplate.query(sql.toString(), params.toArray(), mapper);
    }

    private final RowMapper<TrafficRecord> mapper = new RowMapper<>() {
        @Override
        public TrafficRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrafficRecord t = new TrafficRecord();
            t.setId(rs.getLong("id"));
            t.setGcsj(rs.getTimestamp("gcsj").toLocalDateTime());
            t.setXzqhmc(rs.getString("xzqhmc"));
            t.setAdcode((Integer) rs.getObject("adcode"));
            t.setKkmc(rs.getString("kkmc"));
            t.setStationId((Integer) rs.getObject("station_id"));
            t.setFxlx(rs.getString("fxlx"));
            t.setHpzl(rs.getString("hpzl"));
            t.setHphmMask(rs.getString("hphm_mask"));
            t.setClppxh(rs.getString("clppxh"));
            return t;
        }
    };
}
