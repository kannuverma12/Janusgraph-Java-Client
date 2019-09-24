package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Faq {

    @Field("question_number")
    @JsonProperty("question_number")
    private Integer quesNo;

    @Field("question")
    @JsonProperty("question")
    private String question;

    @Field("answers")
    @JsonProperty("answers")
    private List<String> answers = null;
}
