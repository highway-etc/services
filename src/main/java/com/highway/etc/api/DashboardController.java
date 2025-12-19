package com.highway.etc.api;

import com.highway.etc.api.dto.OverviewResponse;
import com.highway.etc.repository.DashboardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardRepository repository;

    public DashboardController(DashboardRepository repository) {
        this.repository = repository;
    }

    /**
     * 大屏/总览接口：返回近 N 分钟的汇总指标与趋势。
     */
    @GetMapping("/api/overview")
    public OverviewResponse overview(@RequestParam(required = false) Integer windowMinutes) {
        return repository.overview(windowMinutes);
    }
}
