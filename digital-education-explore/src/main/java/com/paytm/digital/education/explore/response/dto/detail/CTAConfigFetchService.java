package com.paytm.digital.education.explore.response.dto.detail;

import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.enums.CTAEntity;

public interface CTAConfigFetchService {
    CTAConfig fetchCTAConfig(CTAEntity ctaEntity, long id);
    CTAConfig fetchCTAConfig(CTAEntity ctaEntity);
}
