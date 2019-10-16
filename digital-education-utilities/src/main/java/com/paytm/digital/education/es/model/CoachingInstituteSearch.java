package com.paytm.digital.education.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.constant.ElasticSearchConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(indexName = ElasticSearchConstants.COACHING_INSTITUTE_INDEX,
        type = ElasticSearchConstants.EDUCATION_TYPE)
public class CoachingInstituteSearch implements IESDocument {

    @Id
    private Long                                coachingInstituteId;
    private String                              brandName;
    private String                              aboutInstitute;
    private List<OfficialAddress>               officialAddress;
    private String                              coverImage;
    private String                              logo;
    private Map<String, Map<String, String>>    streams;
    private List<Long>                          streamIds;
    private List<String>                        streamNames;
    private Map<String, Map<String, String>>    exams;
    private List<Long>                          examIds;
    private List<String>                        examNames;
    private List<String>                        courseTypes;
    private Integer                             establishmentYear;
    private String                              brochure;
    private List<CoachingInstituteKeyHighlight> keyHighlights;
    private List<CoachingInstituteFaq>          faqs;
    private List<String>                        courseLevels;
    private String                              paytmMerchantId;
    private String                              moreInfo1;
    private String                              moreInfo2;
    private String                              moreInfo3;
    private String                              moreInfo4;
    private String                              mongoId;
    private Integer                             globalPriority;
    private Boolean                             isEnabled;

    @Override public String getId() {
        return coachingInstituteId.toString();
    }

    @Override public String getMongoId() {
        return mongoId;
    }

}
