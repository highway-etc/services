package com.highway.etc.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highway.etc.api.dto.StatsResponse;
import com.highway.etc.util.VehicleTypeLabels;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StatsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public StatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StatsResponse> query(Integer stationId, String start, String end) {
        // Prefer realtime stats if available; otherwise, derive on the fly from raw traffic data.
        List<StatsResponse> realtime = queryRealtime(stationId, start, end);
        if (!realtime.isEmpty()) {
            return realtime;
        }
        return queryAggregatedFromTraffic(stationId, start, end);
    }

    private List<StatsResponse> queryRealtime(Integer stationId, String start, String end) {
        StringBuilder sql = new StringBuilder(
                "SELECT station_id,window_start,window_end,cnt AS total_cnt,NULL AS unique_cnt,NULL AS avg_speed,by_dir,by_type "
                + "FROM stats_realtime WHERE 1=1");
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
        return jdbcTemplate.query(sql.toString(), params.toArray(), realtimeMapper);
    }

    private List<StatsResponse> queryAggregatedFromTraffic(Integer stationId, String start, String end) {
        StringBuilder sql = new StringBuilder(
                "SELECT station_id, "
                + "CAST(DATE_FORMAT(gcsj,'%Y-%m-%d %H:%i:00') AS DATETIME) AS window_start, "
                + "CAST(DATE_ADD(DATE_FORMAT(gcsj,'%Y-%m-%d %H:%i:00'), INTERVAL 30 SECOND) AS DATETIME) AS window_end, "
                + "COUNT(*) AS total_cnt, "
                + "COUNT(DISTINCT hphm_mask) AS unique_cnt, "
                + "NULL AS avg_speed, "
                + "NULL AS by_dir, NULL AS by_type "
                + "FROM traffic_pass_dev WHERE 1=1");
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
        sql.append(" GROUP BY station_id, window_start, window_end ORDER BY window_end DESC LIMIT 200");
        return jdbcTemplate.query(sql.toString(), params.toArray(), realtimeMapper);
    }

    private final RowMapper<StatsResponse> realtimeMapper = new RowMapper<>() {
        @Override
        public StatsResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            StatsResponse s = new StatsResponse();
            s.setStationId((Integer) rs.getObject("station_id"));
            s.setWindowStart(rs.getTimestamp("window_start").toLocalDateTime());
            s.setWindowEnd(rs.getTimestamp("window_end").toLocalDateTime());
            s.setTotalCount(rs.getLong("total_cnt"));
            s.setUniquePlates((Long) rs.getObject("unique_cnt"));
            s.setAvgSpeed((Double) rs.getObject("avg_speed"));
            s.setByDir(parseCountMap(rs.getString("by_dir")));
            Map<String, Long> typeMap = parseCountMap(rs.getString("by_type"));
            if (typeMap.isEmpty()) {
                s.setByType(Collections.emptyMap());
            } else {
                Map<String, Long> humanized = new LinkedHashMap<>();
                typeMap.forEach((k, v) -> humanized.put(VehicleTypeLabels.toName(k), v));
                s.setByType(humanized);
            }
            return s;
        }
    };

    private Map<String, Long> parseCountMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return mapper.readValue(json, new TypeReference<Map<String, Long>>() {
            });
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }
}
