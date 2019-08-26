package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.explore.enums.CTAType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CTA {

    private String logo;

    private String label;

    private String url;

    private CTAType type;

    public static class Constants {

        public static final String SHORTLIST = "+ Add To Shortlist";

        public static final String SHORTLISTED = "Shortlisted";

        public static final String GET_UPDATES = "Get Updates";

        public static final String STOP_UPDATES = "Stop Updates";

        public static final String GET_IN_TOUCH = "Get In Touch";

        public static final String INTERESTED = "Interested";

        public static final String PAY_FEE = "Pay Fee";

        public static final String BROCHURE = "Download Brochure";

    }


}
