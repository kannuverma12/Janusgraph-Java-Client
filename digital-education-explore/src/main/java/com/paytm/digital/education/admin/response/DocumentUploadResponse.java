package com.paytm.digital.education.admin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentUploadResponse {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_url")
    private String fileUrl;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("error")
    private String error;

}
