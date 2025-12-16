package com.highway.etc.api;

import com.highway.etc.model.StatsRecord;
import com.highway.etc.repository.StatsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatsController {

    private final StatsRepository repository;

    public StatsController(StatsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/stats")
    public List<StatsRecord> stats(
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        return repository.query(stationId, start, end);
    }
}
