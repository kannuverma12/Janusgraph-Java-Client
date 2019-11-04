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

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(indexName = ElasticSearchConstants.COACHING_CENTER_INDEX,
        type = ElasticSearchConstants.EDUCATION_TYPE)
public class CoachingCenterSearch implements IESDocument {

    public static String OFFICIAL_ADDRESS_KEY = "official_address";

    @Id
    private Long         centerId;
    private Long         instituteId;
    private String       officialName;

    private String       centerImage;
    private String       openingTime;
    private String       closingTime;

    private String       addressLine1;
    private String       addressLine2;
    private String       addressLine3;
    private String       city;
    private String       state;
    private String       pincode;
    private String       email;
    private String       phone;
    private GeoLocation  location;

    private List<Double> sort;
    private String       mongoId;
    private List<String> courseTypes;

    private Boolean      isEnabled;
    private Integer      globalPriority;

    @Override public String getId() {
        return centerId.toString();
    }

    @Override public String getMongoId() {
        return mongoId;
    }
}
