package com.highway.etc.api.dto;

import java.util.List;

public class TrafficPageResponse {

    private long total;
    private List<TrafficResponse> records;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<TrafficResponse> getRecords() {
        return records;
    }

    public void setRecords(List<TrafficResponse> records) {
        this.records = records;
    }
}
