package com.highway.etc.model;

import java.time.LocalDateTime;

public class AlertRecord {

    private Long alertId;
    private String hphmMask;
    private Integer firstStationId;
    private Integer secondStationId;
    private Long timeGapSec;
    private Double distanceKm;
    private Double speedKmh;
    private Double confidence;
    private LocalDateTime createdAt;

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public String getHphmMask() {
        return hphmMask;
    }

    public void setHphmMask(String hphmMask) {
        this.hphmMask = hphmMask;
    }

    public Integer getFirstStationId() {
        return firstStationId;
    }

    public void setFirstStationId(Integer firstStationId) {
        this.firstStationId = firstStationId;
    }

    public Integer getSecondStationId() {
        return secondStationId;
    }

    public void setSecondStationId(Integer secondStationId) {
        this.secondStationId = secondStationId;
    }

    public Long getTimeGapSec() {
        return timeGapSec;
    }

    public void setTimeGapSec(Long timeGapSec) {
        this.timeGapSec = timeGapSec;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getSpeedKmh() {
        return speedKmh;
    }

    public void setSpeedKmh(Double speedKmh) {
        this.speedKmh = speedKmh;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
