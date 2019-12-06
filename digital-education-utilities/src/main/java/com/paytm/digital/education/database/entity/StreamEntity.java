package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@ToString
@Document("stream")
public class StreamEntity extends Base {

    private static final long serialVersionUID = -6827315571130998541L;

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("stream_id")
    @Indexed(unique = true)
    private Long streamId;

    @Field("name")
    private String name;

    @Field("short_name")
    private String shortName;

    @Field("logo")
    private String logo;

}
