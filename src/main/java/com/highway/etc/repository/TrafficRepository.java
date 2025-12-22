package com.highway.etc.repository;

import com.highway.etc.api.dto.TrafficPageResponse;
import com.highway.etc.api.dto.TrafficResponse;
import com.highway.etc.util.VehicleTypeLabels;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TrafficRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final int MAX_PAGE_SIZE = 200;

    public TrafficRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TrafficPageResponse query(Integer stationId, String start, String end, String licensePlate, int page, int size) {
        int safeSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        int safePage = Math.max(page, 0);
        int offset = safePage * safeSize;
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (stationId != null) {
            where.append(" AND station_id = ?");
            params.add(stationId);
        }
        if (start != null && !start.isBlank()) {
            where.append(" AND gcsj >= ?");
            params.add(start);
        }
        if (end != null && !end.isBlank()) {
            where.append(" AND gcsj <= ?");
            params.add(end);
        }
        if (licensePlate != null && !licensePlate.isBlank()) {
            where.append(" AND hphm_mask LIKE ?");
            params.add("%" + licensePlate.trim() + "%");
        }

        String countSql = "SELECT COUNT(*) FROM (SELECT 1 FROM traffic_pass_dev" + where
                + " GROUP BY gcsj,hphm_mask,station_id,xzqhmc,kkmc,hpzl,clppxh,fxlx) t";
        Long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

        StringBuilder sql = new StringBuilder(
                "SELECT gcsj AS timestamp,hphm_mask AS license_plate,station_id,xzqhmc,kkmc,hpzl,clppxh,fxlx,NULL AS speed "
                + "FROM traffic_pass_dev"
                + where
                + " GROUP BY gcsj,hphm_mask,station_id,xzqhmc,kkmc,hpzl,clppxh,fxlx"
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
            t.setXzqhmc(rs.getString("xzqhmc"));
            t.setKkmc(rs.getString("kkmc"));
            t.setSpeed((Double) rs.getObject("speed"));
            String hpzl = rs.getString("hpzl");
            t.setVehicleTypeCode(hpzl);
            t.setVehicleType(VehicleTypeLabels.toName(hpzl));
            t.setVehicleModel(rs.getString("clppxh"));
            t.setDirection(rs.getString("fxlx"));
            return t;
        }
    };
}
