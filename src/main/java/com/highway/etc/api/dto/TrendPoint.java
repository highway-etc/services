package com.highway.etc.api.dto;

import java.time.LocalDateTime;

public class TrendPoint {

    private LocalDateTime windowStart;
    private Long count;

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
