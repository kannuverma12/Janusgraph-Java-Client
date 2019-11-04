package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.database.entity.Lead;
import com.paytm.digital.education.database.entity.UserDetails;

public interface LeadService {

    com.paytm.digital.education.explore.response.dto.common.Lead captureLead(Lead lead);

    com.paytm.digital.education.explore.response.dto.common.Lead unfollowLead(Lead lead);

    UserDetails getUserDetails(Long userId, String email, String firstName, String phone);
}
