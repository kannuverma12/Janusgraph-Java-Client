package com.paytm.digital.education.explore.service.helper.cta.producer;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;

import java.util.Map;

public interface CTAProducer {
    CTA produceCTA(CTAInfoHolder ctaInfoHolder, Map<String, Object> parentCTAConfigMap, Client client);
}
