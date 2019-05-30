package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.database.entity.Lead;

public interface LeadService {
    com.paytm.digital.education.explore.response.dto.common.Lead captureLead(Lead lead);
}
