package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public List<Employee> readAll() {
        LOG.debug("Reading all employees");

        List<Employee> employees = employeeRepository.findAll();

        if (employees == null) {
            throw new RuntimeException("Ruh roh");
        }

        return employees;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        return getFullReportingStructureForLevel(id);
    }

    private ReportingStructure getFullReportingStructureForLevel(String id) {
        Employee employee = read(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        int count = 0;
        ReportingStructure report = new ReportingStructure(employee, count);

        // Reporting employees aren't filled in, except for id, so process each one.
        List<Employee> directReports = employee.getDirectReports();
        if (directReports != null) {
            count = directReports.size();
            report.setNumberOfReports(count);
            for (int i = 0; i < directReports.size(); i++) {
                // Recursively process each direct employee.
                ReportingStructure directStructure = getFullReportingStructureForLevel(directReports.get(i).getEmployeeId());
                // Each level will set the total count of direct employees for the entire level, so just add them to the
                // ones directly managed by this level.
                count += directStructure.getNumberOfReports();
                directReports.set(i, directStructure.getEmployee());
            }
        }

        report.setNumberOfReports(count);
        return report;
    }
}
