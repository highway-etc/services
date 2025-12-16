package com.highway.etc.model;

import java.time.LocalDateTime;

public class StatsRecord {

    private Long id;
    private Integer stationId;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Long cnt;
    private String byDir;
    private String byType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(LocalDateTime windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }

    public String getByDir() {
        return byDir;
    }

    public void setByDir(String byDir) {
        this.byDir = byDir;
    }

    public String getByType() {
        return byType;
    }

    public void setByType(String byType) {
        this.byType = byType;
    }
}
