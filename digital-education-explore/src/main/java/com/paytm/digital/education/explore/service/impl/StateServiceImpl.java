package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.State;
import com.paytm.digital.education.explore.database.repository.StateRepository;
import com.paytm.digital.education.explore.service.StateService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {

    private StateRepository stateRepository;

    @Override
    @Cacheable(value = "states", key = "states", unless = "#result == null")
    public Iterable<State> getAllStates() {
        return stateRepository.findAll();
    }
}
