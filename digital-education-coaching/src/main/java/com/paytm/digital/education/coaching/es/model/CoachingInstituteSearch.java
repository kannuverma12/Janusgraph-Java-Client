package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CourseType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingInstituteSearch {

    private Long                                coachingInstituteId;
    private String                              brandName;
    private String                              aboutInstitute;
    private List<OfficialAddress>               officialAddress;
    private String                              coverImage;
    private String                              logo;
    private Map<String, Map<String, String>>    streams;
    private List<Long>                          streamIds;
    private Map<String, Map<String, String>>    exams;
    private List<Long>                          examIds;
    private List<CourseType>                    courseTypes;
    private Integer                             establishmentYear;
    private String                              brochure;
    private List<CoachingInstituteKeyHighlight> keyHighlights;
    private Integer                             globalPriority;
}
