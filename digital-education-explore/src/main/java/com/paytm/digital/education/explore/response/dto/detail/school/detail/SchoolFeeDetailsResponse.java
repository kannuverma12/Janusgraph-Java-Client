package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.SchoolFeeDetails;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import static com.paytm.digital.education.explore.constants.ExploreConstants.RUPEES_PREFIX;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Getter
public class SchoolFeeDetailsResponse extends SchoolFeeDetails {
    @JsonProperty("fees")
    private String feeAmountWithPrefix;

    public SchoolFeeDetailsResponse(SchoolFeeDetails schoolFeeDetails) {
        super(schoolFeeDetails.getFeeAmount(), schoolFeeDetails.getFeeTenure());
        final Long feeAmount = schoolFeeDetails.getFeeAmount();
        this.setFeeAmount(feeAmount);
    }

    @Override
    public void setFeeAmount(Long feeAmount) {
        super.setFeeAmount(feeAmount);
        this.feeAmountWithPrefix = ObjectUtils.isEmpty(feeAmount)
                ? EMPTY : RUPEES_PREFIX + SPACE + feeAmount;
    }
}
