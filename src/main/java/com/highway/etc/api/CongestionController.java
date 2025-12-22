package com.highway.etc.api;

import com.highway.etc.api.dto.CongestionPoint;
import com.highway.etc.api.dto.DeviceHealthResponse;
import com.highway.etc.repository.CongestionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CongestionController {

    private final CongestionRepository repository;

    public CongestionController(CongestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/congestion")
    public List<CongestionPoint> congestion(
            @RequestParam(required = false) Integer windowMinutes,
            @RequestParam(required = false) Integer stationId
    ) {
        return repository.query(windowMinutes, stationId);
    }

    @GetMapping("/api/device-health")
    public List<DeviceHealthResponse> health(
            @RequestParam(required = false) Integer stationId
    ) {
        return repository.health(stationId);
    }
}
