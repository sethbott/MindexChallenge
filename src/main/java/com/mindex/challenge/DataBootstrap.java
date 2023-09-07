package com.mindex.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.controller.EmployeeController;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
//import java.util.logging.Logger;

@Component
public class DataBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(DataBootstrap.class);
    private static final String EMPLOYEE_DATASTORE_LOCATION = "/static/employee_database.json";
    private static final String COMPENSATION_DATASTORE_LOCATION = "/static/compensation_database.json";

    @Autowired
    private EmployeeRepository employeeRepository;
    private CompensationRepository compensationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try (InputStream inputStream = this.getClass().getResourceAsStream(EMPLOYEE_DATASTORE_LOCATION)) {

            Employee[] employees = null;

            try {
                employees = objectMapper.readValue(inputStream, Employee[].class);
                LOG.debug("Employee array is:");
                for (Employee e : employees) LOG.debug("\t{}", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Employee employee : employees) {
                employeeRepository.insert(employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        LOG.debug("Processing compensations");
//        try (InputStream inputStream = this.getClass().getResourceAsStream(COMPENSATION_DATASTORE_LOCATION)) {
//            LOG.debug("Accessed the file");
//
//            Compensation[] compensations = null;
//
//            try {
//                compensations = objectMapper.readValue(inputStream, Compensation[].class);
//                LOG.debug("Compensation array is:");
//                for (Compensation c : compensations) LOG.debug("\t{}", c);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            try {
//                for (Compensation compensation : compensations) {
//                    compensationRepository.insert(compensation);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
