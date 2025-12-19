package com.highway.etc.api;

import com.highway.etc.api.dto.TrafficPageResponse;
import com.highway.etc.repository.TrafficRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrafficController {

    private final TrafficRepository repository;

    public TrafficController(TrafficRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/traffic")
    public TrafficPageResponse traffic(
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return repository.query(stationId, start, end, licensePlate, page, size);
    }
}
