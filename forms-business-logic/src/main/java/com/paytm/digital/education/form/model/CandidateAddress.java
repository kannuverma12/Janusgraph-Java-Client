package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateAddress {

    private String type;

    private String houseNumber;

    private String building;

    private String street;

    private String landmark;

    private String locality;

    private String state;

    private String district;

    private String villageCity;

    private String pinCode;

    private String taluka;
}
