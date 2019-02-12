package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.database.repository.LeadRepository;
import com.paytm.digital.education.explore.service.LeadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@AllArgsConstructor
public class LeadServiceImpl implements LeadService {
    private LeadRepository leadRepository;

    @Override
    public void captureLead(@NotNull Lead lead) {
        leadRepository.upsertLead(lead);
    }
}
