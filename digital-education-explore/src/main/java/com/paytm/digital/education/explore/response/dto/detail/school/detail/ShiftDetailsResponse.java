package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.ShiftDetails;
import lombok.Data;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_SHIFT_IMAGE_LINK_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EVENING_SHIFT_IMAGE_LINK_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MORNING_SHIFT_IMAGE_LINK_URL;

@Data
public class ShiftDetailsResponse extends ShiftDetails {
    @JsonProperty("shift_image_link_url")
    private String shiftImageLinkUrl;

    public ShiftDetailsResponse(ShiftDetails shiftDetails) {
        super(shiftDetails);
        switch (shiftDetails.getShiftType()) {
            case Morning:
                shiftImageLinkUrl = MORNING_SHIFT_IMAGE_LINK_URL;
                break;
            case Afternoon:
                shiftImageLinkUrl = EVENING_SHIFT_IMAGE_LINK_URL;
                break;
            default:
                shiftImageLinkUrl = DEFAULT_SHIFT_IMAGE_LINK_URL;
        }
    }
}
