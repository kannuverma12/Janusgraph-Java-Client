package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.database.entity.CTAConfigHolder;
import com.paytm.digital.education.database.entity.EducationEntityCTAConfig;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.controller.CTAConfigDBService;
import com.paytm.digital.education.explore.controller.CTAConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.paytm.digital.education.enums.CTAEntity.SCHOOL;
import static com.paytm.digital.education.enums.CTAType.SHORTLIST;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(MockitoJUnitRunner.class)
public class CTAConfigServiceTest {
    private CTAConfigDBService ctaConfigDBService;
    private CTAConfigService ctaConfigService;

    @Mock
    private CommonMongoRepository commonMongoRepository;

    @Mock
    private CommonMongoRepository underlyingCommonMongoRepository;

    @Before
    public void setUp() {
        ctaConfigDBService = new CTAConfigDBService(commonMongoRepository);
        ctaConfigService = new CTAConfigService(ctaConfigDBService);
        ReflectionTestUtils.setField(ctaConfigDBService,
                "underlyingCommonMongoRepository", underlyingCommonMongoRepository);
    }

    @Test
    public void testConfig() {
    }

    @Test(expected = NotFoundException.class)
    public void testEducationEntityCTAConfigNotFound() {
        when(commonMongoRepository.getEntityByFields(
                "cta_entity", SCHOOL.name(), EducationEntityCTAConfig.class, null))
                .thenReturn(null);
        ctaConfigService.getEducationEntityConfig(SCHOOL);
    }

    @Test
    public void testEducationEntityCTAConfigFound() {
        when(commonMongoRepository.getEntityByFields(
                "cta_entity", SCHOOL.name(), EducationEntityCTAConfig.class, null))
                .thenReturn(new EducationEntityCTAConfig(SCHOOL, new CTAConfig(newLinkedHashSet(singletonList(
                        SHORTLIST)))));
        CTAConfig ctaConfig = ctaConfigService.getEducationEntityConfig(SCHOOL);
        assertNotNull(ctaConfig);
        assertEquals(ctaConfig.getCtaTypes(), newLinkedHashSet(singletonList(SHORTLIST)));
    }

    @Test
    public void testPutEducationEntityConfigNew() {
        when(underlyingCommonMongoRepository.getEntityByFields(
                "cta_entity", SCHOOL.name(), EducationEntityCTAConfig.class, null))
                .thenReturn(null);

        ctaConfigService.putEducationEntityConfig(SCHOOL, new CTAConfig(newLinkedHashSet(singletonList(SHORTLIST))));
        ArgumentCaptor<CTAConfigHolder> argument = ArgumentCaptor.forClass(CTAConfigHolder.class);
        verify(commonMongoRepository).saveOrUpdate(argument.capture());
        assertNotNull(argument.getValue());
        assertThat(argument.getValue(), instanceOf(EducationEntityCTAConfig.class));
        assertEquals(argument.getValue().getCTAConfig().getCtaTypes(), newLinkedHashSet(singletonList(SHORTLIST)));
    }
}
