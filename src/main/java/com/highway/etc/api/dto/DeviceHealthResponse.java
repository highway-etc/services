package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class DeviceHealthResponse {

    private Integer stationId;
    private String stationName;
    private LocalDateTime lastHeartbeat;
    private Double uptimePct;
    private Double errorRate;
    private Boolean maintenanceFlag;
    private String status;

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

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Double getUptimePct() {
        return uptimePct;
    }

    public void setUptimePct(Double uptimePct) {
        this.uptimePct = uptimePct;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public Boolean getMaintenanceFlag() {
        return maintenanceFlag;
    }

    public void setMaintenanceFlag(Boolean maintenanceFlag) {
        this.maintenanceFlag = maintenanceFlag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
