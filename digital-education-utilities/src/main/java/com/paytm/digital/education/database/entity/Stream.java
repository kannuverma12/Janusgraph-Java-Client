package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document("stream")
@Builder
public class Stream extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("stream_id")
    @JsonProperty("stream_id")
    private Long streamId;

    @Indexed(unique = true)
    private String name;

    private String logo;

    @JsonProperty("top_institutes")
    @Field("top_institutes")
    private List<Long> topInstitutes;
}
