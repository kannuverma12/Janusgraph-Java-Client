package com.paytm.digital.education.explore.service.helper;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAConfigFetchService;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.service.helper.cta.producer.BrochureCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.CTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.CompareCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.FeeCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.FormCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.LeadCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.PredictorCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.ShareCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.ShortListCTAProducer;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.enums.CTAType.BROCHURE;
import static com.paytm.digital.education.enums.CTAType.COMPARE;
import static com.paytm.digital.education.enums.CTAType.FEE;
import static com.paytm.digital.education.enums.CTAType.FORMS;
import static com.paytm.digital.education.enums.CTAType.LEAD;
import static com.paytm.digital.education.enums.CTAType.PREDICTOR;
import static com.paytm.digital.education.enums.CTAType.SHARE;
import static com.paytm.digital.education.enums.CTAType.SHORTLIST;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class CTAHelper {

    private final PropertyReader propertyReader;

    private final CTAConfigFetchService ctaConfigFetchService;

    private final Map<CTAType, CTAProducer> ctaTypeCTAProducerMap;

    private static Logger log = LoggerFactory.getLogger(CTAHelper.class);

    public CTAHelper(
            PropertyReader propertyReader, BrochureCTAProducer brochureCTAProducer,
            CompareCTAProducer compareCTAProducer, FeeCTAProducer feeCTAProducer, FormCTAProducer formCTAProducer,
            LeadCTAProducer leadCTAProducer, PredictorCTAProducer predictorCTAProducer,
            ShareCTAProducer shareCTAProducer, ShortListCTAProducer shortListCTAProducer,
            CTAConfigFetchService ctaConfigFetchService) {
        this.propertyReader = propertyReader;
        this.ctaConfigFetchService = ctaConfigFetchService;

        this.ctaTypeCTAProducerMap = ImmutableMap.<CTAType, CTAProducer>builder()
                .put(BROCHURE, brochureCTAProducer)
                .put(COMPARE, compareCTAProducer)
                .put(FEE, feeCTAProducer)
                .put(FORMS, formCTAProducer)
                .put(LEAD, leadCTAProducer)
                .put(PREDICTOR, predictorCTAProducer)
                .put(SHARE, shareCTAProducer)
                .put(SHORTLIST, shortListCTAProducer)
                .build();
    }


    public List<CTA> buildCTA(CTAInfoHolder ctaInfoHolder, Client client) {

        CTAConfig ctaConfig = ctaInfoHolder.getCTAConfig(ctaConfigFetchService);
        CTAConfig finalCTAConfig = isEmpty(ctaConfig.getCtaTypes())
                ? ctaInfoHolder.getEntityLevelCTAConfig(ctaConfigFetchService) : ctaConfig;

        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Map<String, Object> ctaConfigurationMap = propertyReader.getPropertiesAsMapByKey(
                ExploreConstants.EXPLORE_COMPONENT, namespace, key);
        return finalCTAConfig.getCtaTypes()
                .stream()
                .map(ctaTypeCTAProducerMap::get)
                .map(ctaProducer -> ctaProducer.produceCTA(ctaInfoHolder, ctaConfigurationMap, client))
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
