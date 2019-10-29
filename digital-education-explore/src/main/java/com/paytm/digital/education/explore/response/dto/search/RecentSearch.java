package com.paytm.digital.education.explore.response.dto.search;

import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;

@Data
public class RecentSearch extends SearchBaseData {

    private String id;

    private String term;

    private EducationEntity entity;

}
