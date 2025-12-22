package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class StatsResponse {

    private Integer stationId;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Long totalCount;
    private Long uniquePlates;
    private Double avgSpeed;
    private java.util.Map<String, Long> byDir;
    private java.util.Map<String, Long> byType;

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

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getUniquePlates() {
        return uniquePlates;
    }

    public void setUniquePlates(Long uniquePlates) {
        this.uniquePlates = uniquePlates;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public java.util.Map<String, Long> getByDir() {
        return byDir;
    }

    public void setByDir(java.util.Map<String, Long> byDir) {
        this.byDir = byDir;
    }

    public java.util.Map<String, Long> getByType() {
        return byType;
    }

    public void setByType(java.util.Map<String, Long> byType) {
        this.byType = byType;
    }
}
