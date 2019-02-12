package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.State;
import com.paytm.digital.education.explore.database.repository.StateRepository;
import com.paytm.digital.education.explore.service.StateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {

    private StateRepository stateRepository;

    @Override
    public Iterable<State> getAllStates() {
        return stateRepository.findAll();
    }
}
