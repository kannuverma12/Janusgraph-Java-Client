package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@ToString
@Document("failed_data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FailedData {
    @Field("component")
    private String component;

    @Field("type")
    private String type;

    @Field("has_imported")
    private Boolean hasImported;

    @Field("is_importable")
    private Boolean isImportable;

    @Field("failed_date")
    private Date failedDate;

    @Field("message")
    private String message;

    @Field("data")
    private Object data;
}
