package com.paytm.digital.education.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.constant.ElasticSearchConstants;
import com.paytm.digital.education.constant.ExploreConstants;
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
@Document(indexName = ExploreConstants.SEARCH_INDEX_EXAM,
            type = ElasticSearchConstants.EDUCATION_TYPE)
public class ExamSearch implements IESDocument {

    @JsonProperty("exam_id")
    private int examId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("names")
    private List<String> names;

    @JsonProperty("image_link")
    private String imageLink;

    @JsonProperty("linguistic_medium")
    private List<String> linguisticMedium;

    @JsonProperty("level")
    private String level;

    @JsonProperty("instances")
    private List<ExamInstance> examInstances;

    @JsonProperty("tabs_available")
    private List<String> dataAvailable;

    @JsonProperty("global_priority")
    private Integer globalPriority;

    @JsonProperty("stream_ids")
    private List<Long> streamIds;

    @JsonProperty("stream_names")
    private List<String> streamNames;

    @JsonProperty("streams")
    private Map<String, Map<String, Long>> streams;

    @Id
    @JsonProperty("mongo_id")
    private String mongoId;

    @Override public String getId() {
        return mongoId;
    }

    @Override public String getMongoId() {
        return mongoId;
    }
}
