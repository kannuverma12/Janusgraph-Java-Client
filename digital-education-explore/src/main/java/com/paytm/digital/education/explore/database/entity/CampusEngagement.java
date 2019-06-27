package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class CampusEngagement {

    @Field("institute_id")
    private Long instituteId;

    @Field("campus_ambassadors")
    private Map<String, CampusAmbassador> campusAmbassadors;

    @Field("articles")
    private List<Article> articles;

    @Field("events")
    private List<CampusEvent> events;
}
