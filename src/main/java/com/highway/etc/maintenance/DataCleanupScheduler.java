package com.highway.etc.maintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically purge aged data to keep pipelines flowing and MyCat shards lean.
 */
@Component
public class DataCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataCleanupScheduler.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${maintenance.retention-days:3}")
    private int retentionDays;

    public DataCleanupScheduler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedDelayString = "${maintenance.cleanup-interval-ms:1800000}")
    public void cleanupOldRows() {
        int days = Math.max(retentionDays, 1);
        try {
            int traffic = jdbcTemplate.update(
                    "DELETE FROM traffic_pass_dev WHERE gcsj < DATE_SUB(NOW(), INTERVAL ? DAY)", days);
            int stats = jdbcTemplate.update(
                    "DELETE FROM stats_realtime WHERE window_end < DATE_SUB(NOW(), INTERVAL ? DAY)", days);
            int alerts = jdbcTemplate.update(
                    "DELETE FROM alert_plate_clone WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)", days);
            log.info("Data cleanup done: traffic={}, stats={}, alerts={}, retentionDays={}", traffic, stats, alerts, days);
        } catch (Exception ex) {
            log.warn("Data cleanup failed", ex);
        }
    }
}
