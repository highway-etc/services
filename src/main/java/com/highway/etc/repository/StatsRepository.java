package com.highway.etc.repository;

import com.highway.etc.model.StatsRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public StatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StatsRecord> query(Integer stationId, String start, String end) {
        StringBuilder sql = new StringBuilder("SELECT id,station_id,window_start,window_end,cnt,by_dir,by_type FROM stats_realtime WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        if (start != null && !start.isBlank()) {
            sql.append(" AND window_start >= ?");
            params.add(start);
        }
        if (end != null && !end.isBlank()) {
            sql.append(" AND window_end <= ?");
            params.add(end);
        }
        sql.append(" ORDER BY window_end DESC LIMIT 200");
        return jdbcTemplate.query(sql.toString(), params.toArray(), mapper);
    }

    private final RowMapper<StatsRecord> mapper = new RowMapper<>() {
        @Override
        public StatsRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            StatsRecord s = new StatsRecord();
            s.setId(rs.getLong("id"));
            s.setStationId((Integer) rs.getObject("station_id"));
            s.setWindowStart(rs.getTimestamp("window_start").toLocalDateTime());
            s.setWindowEnd(rs.getTimestamp("window_end").toLocalDateTime());
            s.setCnt(rs.getLong("cnt"));
            s.setByDir(rs.getString("by_dir"));
            s.setByType(rs.getString("by_type"));
            return s;
        }
    };
}
