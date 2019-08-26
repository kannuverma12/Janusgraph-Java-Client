package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaytmKeys {

    public static class Constants {

        public static final String PAYTM_KEYS = "paytm_keys";

    }

}
