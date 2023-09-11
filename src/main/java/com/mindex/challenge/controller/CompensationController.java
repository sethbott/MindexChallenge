package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/compensation/{id}")
    public Compensation createCompensation(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for id [{}]", compensation.getEmployeeId());

        return compensationService.createCompensation(compensation);
    }

    @GetMapping("/compensation/{id}")
    public Compensation getCompensation(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        return compensationService.getCompensation(id);
    }

    @PutMapping("/compensation/{id}")
    public Compensation updateCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation update request for id [{}]", id);

        return compensationService.setCompensation(compensation);
    }

    @GetMapping("/compensation/compensations")
    public List<Compensation> readAll() {
        LOG.debug("Received compensation read request for ALL");

        return compensationService.readAll();
    }

}
