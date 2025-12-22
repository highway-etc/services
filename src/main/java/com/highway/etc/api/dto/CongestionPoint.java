package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class CongestionPoint {

    private Integer stationId;
    private String stationName;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Double congestionIndex;
    private String level;
    private Double flowPerMinute;
    private Double avgSpeedKmh;
    private Double occupancy;
    private Double healthScore;

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
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

    public Double getCongestionIndex() {
        return congestionIndex;
    }

    public void setCongestionIndex(Double congestionIndex) {
        this.congestionIndex = congestionIndex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getFlowPerMinute() {
        return flowPerMinute;
    }

    public void setFlowPerMinute(Double flowPerMinute) {
        this.flowPerMinute = flowPerMinute;
    }

    public Double getAvgSpeedKmh() {
        return avgSpeedKmh;
    }

    public void setAvgSpeedKmh(Double avgSpeedKmh) {
        this.avgSpeedKmh = avgSpeedKmh;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

    public Double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Double healthScore) {
        this.healthScore = healthScore;
    }
}
