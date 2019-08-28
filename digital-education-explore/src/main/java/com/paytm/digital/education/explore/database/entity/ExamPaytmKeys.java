package com.paytm.digital.education.explore.database.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
public class ExamPaytmKeys extends PaytmKeys {

    @Field("college_predictor_id")
    private Long collegePredictorId;

    @Field("form_id")
    private String formId;

}
