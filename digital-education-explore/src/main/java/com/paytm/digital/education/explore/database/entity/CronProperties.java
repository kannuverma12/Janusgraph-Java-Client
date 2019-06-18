package com.paytm.digital.education.explore.database.entity;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Document(collection = "cron_properties")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CronProperties {
    
    @Field("cron_name")
    private String cronName;
    
    @Field("is_active")
    private Boolean isActive; 
    
    @Field("created_at")
    private Date createdAt;
    
    @Field("updated_at")
    private Date updatedAt;

    
    
}
