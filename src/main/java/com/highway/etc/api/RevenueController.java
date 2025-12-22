package com.highway.etc.api;

import com.highway.etc.api.dto.RevenuePoint;
import com.highway.etc.repository.RevenueRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RevenueController {

    private final RevenueRepository repository;

    public RevenueController(RevenueRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/revenue/forecast")
    public List<RevenuePoint> forecast(
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer windowMinutes
    ) {
        return repository.forecast(stationId, windowMinutes);
    }
}
