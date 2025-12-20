package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class TrafficResponse {

    private LocalDateTime timestamp;
    private String licensePlate;
    private Integer stationId;
    private String xzqhmc;
    private String kkmc;
    private Double speed;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getXzqhmc() {
        return xzqhmc;
    }

    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }

    public String getKkmc() {
        return kkmc;
    }

    public void setKkmc(String kkmc) {
        this.kkmc = kkmc;
    }

    public Double getSpeed() {

    

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
