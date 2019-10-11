package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
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

    @Field("paytm_stream_id")
    @JsonProperty("paytm_stream_id")
    private Long paytmStreamId;

    @Field("paytm_stream")
    @JsonProperty("paytm_stream")
    private String paytmStream;

    @Field("merchant_stream")
    @JsonProperty("merchant_stream")
    private String merchantStream;

    public ExamStreamEntity() {
        this.setCreatedAt(LocalDateTime.now());
    }
}
