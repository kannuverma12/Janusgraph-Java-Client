package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.admin.request.EntitySourceMappingData;
import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.admin.validator.EntitySourceMappingDataValidator;
import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.search.ExamData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EntitySourceType.PAYTM;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntitySourceMappingDataValidatorTest {
    @Mock
    private PaytmSourceDataRepository paytmSourceDataRepository;
    @Mock
    private CommonMongoRepository     commonMongoRepository;
    @InjectMocks
    private EntitySourceMappingDataValidator validator;

    @Test
    public void testExamValidationSuccess(){
        EntitySourceMappingRequest entitySourceMappingRequest = new EntitySourceMappingRequest();
        entitySourceMappingRequest.setEducationEntity(EducationEntity.EXAM);

        entitySourceMappingRequest.setEducationEntity(EXAM);
        entitySourceMappingRequest
                .setEntitySourceMappingData(Arrays.asList(new EntitySourceMappingData(1l,
                        PAYTM)));
        PaytmSourceDataEntity paytmSourceDataEntity = new PaytmSourceDataEntity();
        paytmSourceDataEntity.setEntityId(1l);
        paytmSourceDataEntity.setActive(true);
        paytmSourceDataEntity.setExamData(new Exam());

        when(paytmSourceDataRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(paytmSourceDataEntity);
        validator.validateEntitySourceMappingData(entitySourceMappingRequest);
    }

}
