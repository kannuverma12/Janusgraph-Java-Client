package com.paytm.digital.education.coaching.consumer.model.response.suggest;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AutoSuggestData {

    private String entityType;
    private List<SuggestResult> results;
}
