package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

import java.util.List;

public interface CompensationService {
    Compensation createCompensation(Compensation compensation);
    Compensation getCompensation(String id);
    Compensation setCompensation(Compensation compensation);
    List<Compensation> readAll();
}
