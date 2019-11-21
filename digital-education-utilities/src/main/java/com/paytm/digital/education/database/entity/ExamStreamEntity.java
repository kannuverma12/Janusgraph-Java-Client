package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Document("exam_stream_mapping")
public class ExamStreamEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("exam_id")
    @Indexed(unique = true)
    private Long examId;

    @Field("exam_full_name")
    @JsonProperty("exam_full_name")
    private String examFullName;

    @Field("exam_short_name")
    @JsonProperty("exam_short_name")
    private String examShortName;

    @Field("paytm_stream_ids")
    @JsonProperty("paytm_stream_ids")
    private List<Long> paytmStreamIds;

    public ExamStreamEntity() {
        this.setCreatedAt(LocalDateTime.now());
    }
}
