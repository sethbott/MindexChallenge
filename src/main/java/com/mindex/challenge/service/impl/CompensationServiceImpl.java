package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public List<Compensation> readAll() {
        LOG.debug("Reading all compensations");

        List<Compensation> compensations = compensationRepository.findAll();

        if (compensations == null) {
            throw new RuntimeException("Ruh roh");
        }

        return compensations;
    }

    @Override
    public Compensation createCompensation(Compensation compensation) {

        //LOG.debug("Creating compensation for employee [{}] to {} with an effective date of {}",
        //          compensation.getEmployeeId(), compensation.getSalary(), compensation.getEffectiveDate());
        LOG.debug("Creating compensation [{}]", compensation);

        compensationRepository.insert(compensation);

        LOG.debug("Returning compensation: " + compensation);

        return compensation;
    }

    @Override
    public Compensation getCompensation(String id) {
        LOG.debug("Reading compensation for employee [{}]", id);

        Compensation comp = compensationRepository.findByEmployeeId(id);

        if (comp == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return comp;
    }

    @Override
    public Compensation setCompensation(Compensation compensation) {

        LOG.debug("Changing compensation for employee [{}] to {} with an effective date of {}",
                  compensation.getEmployeeId(), compensation.getSalary(), compensation.getEffectiveDate());

        compensationRepository.save(compensation);

        return compensation;
    }
}
