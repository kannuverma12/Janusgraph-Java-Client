package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.database.entity.ShiftDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShiftDetailsResponse extends ShiftDetails {
    @JsonProperty("shift_image_link_url")
    private String shiftImageLinkUrl;

    public ShiftDetailsResponse(ShiftDetails shiftDetails, SchoolConfig schoolConfig) {
        super(shiftDetails);
        switch (shiftDetails.getShiftType()) {
            case Morning:
                shiftImageLinkUrl = schoolConfig.getMorningShiftImageURL();
                break;
            case Afternoon:
                shiftImageLinkUrl = schoolConfig.getEveningShiftImageURL();
                break;
            default:
                shiftImageLinkUrl = schoolConfig.getDefaultShiftImageURL();
        }
    }
}
