package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CTAType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CTA implements Serializable {

    private static final long serialVersionUID = -6699794153754403121L;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("activeLogo")
    private String activeLogo;

    @JsonProperty("label")
    private String label;

    @JsonProperty("activeText")
    private String activeText;

    @JsonProperty("url")
    private String url;

    @JsonProperty("type")
    private CTAType type;


    public static class Constants {
        public static final String ICON                = "icon";
        public static final String ACTIVE_ICON         = "active_icon";
        public static final String ACTIVE_DISPLAY_NAME = "active_display_name";
        public static final String DISPLAY_NAME        = "display_name";
        public static final String WEB                 = "_web";
        public static final String CLIENT              = "_client";
    }


}
