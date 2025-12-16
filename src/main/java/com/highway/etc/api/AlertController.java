package com.highway.etc.api;

import com.highway.etc.model.AlertRecord;
import com.highway.etc.repository.AlertRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {

    private final AlertRepository repository;

    public AlertController(AlertRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/alerts")
    public List<AlertRecord> alerts(
            @RequestParam(required = false) String plate,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        return repository.query(plate, start, end);
    }
}
