package com.paytm.digital.education.explore.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPaytmKeys extends PaytmKeys {

    @Field("college_predictor_id")
    private Long collegePredictorId;

    @Field("form_id")
    private String formId;

}
