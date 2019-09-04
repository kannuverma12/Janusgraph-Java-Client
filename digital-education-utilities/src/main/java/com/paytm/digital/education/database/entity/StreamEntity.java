package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("stream")
public class StreamEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("stream_id")
    @Indexed(unique = true)
    private Long streamId;

    @Indexed(unique = true)
    private String name;

    private String logo;

}
