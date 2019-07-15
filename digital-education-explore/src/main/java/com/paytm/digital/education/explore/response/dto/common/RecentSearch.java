package com.paytm.digital.education.explore.response.dto.common;

import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;

@Data
public class RecentSearch {

    private String term;

    private EducationEntity entity;

}
