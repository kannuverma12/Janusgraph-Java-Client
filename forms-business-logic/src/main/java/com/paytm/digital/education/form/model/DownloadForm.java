package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Document("formData")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DownloadForm {

    //TODO: Discuss about orderId
    private Long orderId;

    // TODO: Add CDN for paytm logon
    private String paytmLogo;

    // TODO: DMS data (photo and candidate)
    private Map<String, Object> candidateData;

    private String fullName;
    private String email;
    private String mobileNumber;
    private String dateOfBirth;
    private String password;
    private String salutation;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    @Field("motherName")
    private String mothersName;
    @Field("fatherName")
    private String fathersName;
    private String gender;
    private Integer age;
    private String landLineNumber;
    private Boolean isMarried;
    private Boolean isPwd;
    private String religion;
    private String category;
    private String nationality;
    @Field("instName")
    private String merchantName;

}