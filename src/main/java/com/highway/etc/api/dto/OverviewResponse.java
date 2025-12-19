package com.highway.etc.api.dto;

import java.util.List;

public class OverviewResponse {

    private long totalTraffic;
    private Long uniquePlates;
    private long alertCount;
    private List<TopStation> topStations;
    private List<TrendPoint> trafficTrend;

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public void setTotalTraffic(long totalTraffic) {
        this.totalTraffic = totalTraffic;
    }

    public Long getUniquePlates() {
        return uniquePlates;
    }

    public void setUniquePlates(Long uniquePlates) {
        this.uniquePlates = uniquePlates;
    }

    public long getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(long alertCount) {
        this.alertCount = alertCount;
    }

    public List<TopStation> getTopStations() {
        return topStations;
    }

    public void setTopStations(List<TopStation> topStations) {
        this.topStations = topStations;
    }

    public List<TrendPoint> getTrafficTrend() {
        return trafficTrend;
    }

    public void setTrafficTrend(List<TrendPoint> trafficTrend) {
        this.trafficTrend = trafficTrend;
    }
}
