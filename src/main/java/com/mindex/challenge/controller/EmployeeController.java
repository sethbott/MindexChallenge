package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.error("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }

    @GetMapping("/employees")
    public List<Employee> readAll() {
        LOG.debug("Received employee read request for ALL");

        return employeeService.readAll();
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }

    @GetMapping("/employee/structure/{id}")
    public ReportingStructure getReportingStructure(@PathVariable String id) {
        LOG.debug("Received full reporting structure read request for id [{}]", id);

        return employeeService.getReportingStructure(id);
    }

    @GetMapping("/employee/salary/{id}")
    public Compensation getCompensation(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        return employeeService.getCompensation(id);
    }

    @PutMapping("/employee/salary/{id}")
    //public Compensation updateCompensation(@PathVariable String id, @PathVariable String newSalary, @PathVariable String newDate) {
    public Compensation updateCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation update request for id [{}]", id);

        return employeeService.setCompensation(compensation);
    }

}
