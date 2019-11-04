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

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(indexName = ElasticSearchConstants.STREAM_INDEX,
        type = ElasticSearchConstants.EDUCATION_TYPE)
public class StreamSearch implements IESDocument {

    @Id
    private Long streamId;

    private String name;

    private String logo;

    private String mongoId;

    @Override public String getId() {
        return streamId.toString();
    }

    @Override public String getMongoId() {
        return mongoId;
    }
}
