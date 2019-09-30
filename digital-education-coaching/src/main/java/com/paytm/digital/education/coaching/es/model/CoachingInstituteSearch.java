package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingInstituteSearch {

    private Long                             coachingInstituteId;
    private String                           brandName;
    private String                           logo;
    private Map<String, Map<String, String>> streams;
    private List<Long>                       streamIds;
    private List<String>                     streamNames;
    private Map<String, Map<String, String>> exams;
    private List<Long>                       examIds;
    private List<String>                     examNames;
    private List<String>                     courseTypes;
    private Integer                          globalPriority;
    private Boolean                          isEnabled;
}
