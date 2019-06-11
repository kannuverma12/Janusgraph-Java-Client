package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.form.enums.DMSDocType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMSDoc {

    private DMSDocType docType;

    private String dmsId;

    private String fileName;

    private String fileStream;

    private String ext;

    private String mimeType;

    private boolean isSuccess = false;
}
