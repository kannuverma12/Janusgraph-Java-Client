package com.paytm.digital.education.coaching.consumer.model.response.suggest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SuggestResult {

    private long   entityId;
    private String urlDisplayKey;
    private String officialName;
    private String logo;

    public SuggestResult(long entityId, String officialName) {
        this.entityId = entityId;
        this.officialName = officialName;
    }

}
