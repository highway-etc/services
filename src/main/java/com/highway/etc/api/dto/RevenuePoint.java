package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class RevenuePoint {

    private Integer stationId;
    private String stationName;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private String vehicleType;
    private Long trafficCount;
    private Double revenue;
    private Double forecastRevenue;

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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Long getTrafficCount() {
        return trafficCount;
    }

    public void setTrafficCount(Long trafficCount) {
        this.trafficCount = trafficCount;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getForecastRevenue() {
        return forecastRevenue;
    }

    public void setForecastRevenue(Double forecastRevenue) {
        this.forecastRevenue = forecastRevenue;
    }
}
