package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CTAType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CTA {

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

        public static final String SHORTLIST = "+ Add To Shortlist";

        public static final String SHORTLIST_APP = "Shortlist College";

        public static final String SHORTLIST_SCHOOL_APP = "Shortlist School";

        public static final String SHORTLISTED = "Shortlisted";

        public static final String SHORTLISTED_APP = "Shortlisted College";

        public static final String SHORTLISTED_SCHOOL_APP = "Shortlisted School";

        public static final String SHORTLIST_EXAM_APP = "Shortlist Exam";

        public static final String SHORTLISTED_EXAM_APP = "Shortlisted Exam";

        public static final String GET_UPDATES = "Get Update";

        public static final String STOP_UPDATES = "Stop Update";

        public static final String GET_IN_TOUCH = "Get In Touch";

        public static final String INQUIRY_SENT = "Inquiry Sent";

        public static final String STP_UPDATES = "Stop Updates";

        public static final String INTERESTED = "Interested";

        public static final String PAY_FEE = "Pay Fee";

        public static final String BROCHURE = "Download Brochure";

        public static final String COMPARE = "+ Add To Compare";

        public static final String COMPARE_ACTIVE_LABEL_WEB = "Added To Compare";

        public static final String APPLY = "Apply";

        public static final String COMPARE_APP = "Compare";

        public static final String PREDICT_COLLEGE = "Predict College";

    }


}
