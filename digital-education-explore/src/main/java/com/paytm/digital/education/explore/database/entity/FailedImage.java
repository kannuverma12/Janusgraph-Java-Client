package com.paytm.digital.education.explore.database.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class FailedImage {

    
    @Field("institute_id")
    private Long instituteId;
   
    @Field("image_url")
    private String imageUrl;
    
    @Field("type")
    private String type;

    @Field("reason")
    private String reason;

    public FailedImage(Long instituteId, String imageUrl, String type, String reason) {
        super();
        this.instituteId = instituteId;
        this.imageUrl = imageUrl;
        this.type = type;
        this.reason = reason;
    }
    
    
    
}
