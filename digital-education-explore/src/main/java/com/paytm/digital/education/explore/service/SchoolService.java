package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;

import java.util.List;

public interface SchoolService {
    SchoolDetail getSchoolDetails(
            Long schoolId, Client client, String schoolName, List<String> fields, String fieldGroup);
}
