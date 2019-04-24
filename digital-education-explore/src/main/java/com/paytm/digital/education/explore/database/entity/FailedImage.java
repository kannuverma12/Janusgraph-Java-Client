package com.paytm.digital.education.explore.database.entity;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class FailedImage {
    
    @Id
    @Field("_id")
    @JsonIgnore
    private String id;
    
    @Field("institute_id")
    private Long instituteId;
   
    @Field("image_url")
    private String imageUrl;
    
    @Field("type")
    private String type;

    @Field("reason")
    private String reason;
    
    @Field("is_deleted")
    private Boolean isDeleted;
    
    @Field("retry_count")
    private Integer retryCount;
    
    @Field("created_at")
    private Date createdAt;
    
    @Field("last_updated_at")
    private Date lastUpdatedAt;
   

    public FailedImage(Long instituteId, String imageUrl, String type, String reason) {
        super();
        this.instituteId = instituteId;
        this.imageUrl = imageUrl;
        this.type = type;
        this.reason = reason;
    }
    
    
    
}
