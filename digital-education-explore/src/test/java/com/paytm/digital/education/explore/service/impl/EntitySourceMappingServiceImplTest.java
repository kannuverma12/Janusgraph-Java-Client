package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.admin.request.EntitySourceMappingData;
import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.admin.response.EntitySourceMappingResponse;
import com.paytm.digital.education.admin.service.impl.EntitySourceMappingServiceImpl;
import com.paytm.digital.education.admin.validator.EntitySourceMappingDataValidator;
import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static com.paytm.digital.education.constant.ExploreConstants.FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.SUCCESS;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EntitySourceType.C360;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntitySourceMappingServiceImplTest {
    @Mock
    private EntitySourceMappingRepository    entitySourceMappingRepository;
    @Mock
    private EntitySourceMappingDataValidator validator;
    @InjectMocks
    private EntitySourceMappingServiceImpl   entitySourceMappingService;

    @Test
    public void testSaveEntitySourceMappingSuccess() {
        EntitySourceMappingRequest entitySourceMappingRequest = new EntitySourceMappingRequest();
        entitySourceMappingRequest.setEducationEntity(EXAM);
        entitySourceMappingRequest
                .setEntitySourceMappingData(Arrays.asList(new EntitySourceMappingData(1l,
                        C360)));

        EntitySourceMappingEntity mappingEntityInDb = new EntitySourceMappingEntity();
        mappingEntityInDb.setEducationEntity(EXAM);
        mappingEntityInDb.setEntityId(1l);
        mappingEntityInDb.setActive(false);

        EntitySourceMappingEntity mappingEntityDbResponse = new EntitySourceMappingEntity();
        mappingEntityDbResponse.setEducationEntity(EXAM);
        mappingEntityDbResponse.setEntityId(1l);
        mappingEntityDbResponse.setActive(true);

        doNothing().when(validator)
                .validateEntitySourceMappingData(any(EntitySourceMappingRequest.class));
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(mappingEntityInDb);
        when(entitySourceMappingRepository.save(any(EntitySourceMappingEntity.class)))
                .thenReturn(mappingEntityDbResponse);

        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.saveEntitySourceMapping(entitySourceMappingRequest);
        assertNotNull(entitySourceMappingResponse);
        assertEquals(SUCCESS, entitySourceMappingResponse.getStatus());
        assertEquals(EXAM, entitySourceMappingResponse.getEducationEntity());
        assertEquals(mappingEntityDbResponse, entitySourceMappingResponse.getData().get(0));
    }

    @Test
    public void testSaveEntitySourceMappingFailure() {
        EntitySourceMappingRequest entitySourceMappingRequest = new EntitySourceMappingRequest();
        entitySourceMappingRequest.setEducationEntity(EXAM);
        entitySourceMappingRequest
                .setEntitySourceMappingData(Arrays.asList(new EntitySourceMappingData(1l,
                        C360)));

        EntitySourceMappingEntity mappingEntityInDb = new EntitySourceMappingEntity();
        mappingEntityInDb.setEducationEntity(EXAM);
        mappingEntityInDb.setEntityId(1l);
        mappingEntityInDb.setActive(false);

        doNothing().when(validator)
                .validateEntitySourceMappingData(any(EntitySourceMappingRequest.class));
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(mappingEntityInDb);
        when(entitySourceMappingRepository.save(any(EntitySourceMappingEntity.class)))
                .thenThrow(RuntimeException.class);

        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.saveEntitySourceMapping(entitySourceMappingRequest);
        assertNotNull(entitySourceMappingResponse);
        assertEquals(FAILED, entitySourceMappingResponse.getStatus());
        assertEquals(EXAM, entitySourceMappingResponse.getEducationEntity());
        assertEquals(null, entitySourceMappingResponse.getData());
    }

    @Test
    public void testGetEntitySourceMappingSuccess() {
        EntitySourceMappingEntity entitySourceMappingEntity = new EntitySourceMappingEntity();
        entitySourceMappingEntity.setActive(true);
        entitySourceMappingEntity.setEntityId(1l);
        entitySourceMappingEntity.setEducationEntity(EXAM);
        entitySourceMappingEntity.setSource(C360);
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(entitySourceMappingEntity);

        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.getEntitySourceMapping(EXAM, 1l);
        assertNotNull(entitySourceMappingResponse);
        assertNotNull(entitySourceMappingResponse.getData());
        assertEquals(SUCCESS, entitySourceMappingResponse.getStatus());
        assertEquals(1, entitySourceMappingResponse.getData().size());
        assertEquals(entitySourceMappingEntity, entitySourceMappingResponse.getData().get(0));
    }

    @Test
    public void testGetEntitySourceMappingFailure() {
        EntitySourceMappingEntity entitySourceMappingEntity = new EntitySourceMappingEntity();
        entitySourceMappingEntity.setActive(true);
        entitySourceMappingEntity.setEntityId(1l);
        entitySourceMappingEntity.setEducationEntity(EXAM);
        entitySourceMappingEntity.setSource(C360);
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(null);

        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.getEntitySourceMapping(EXAM, 1l);
        assertNotNull(entitySourceMappingResponse);
        assertEquals(null, entitySourceMappingResponse.getData());
        assertEquals(FAILED, entitySourceMappingResponse.getStatus());
    }

    @Test
    public void testDeleteEntitySourceMappingSuccess() {

        EntitySourceMappingEntity entitySourceMappingEntity = new EntitySourceMappingEntity();
        entitySourceMappingEntity.setActive(true);
        entitySourceMappingEntity.setEntityId(1l);
        entitySourceMappingEntity.setEducationEntity(EXAM);
        entitySourceMappingEntity.setSource(C360);
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(entitySourceMappingEntity);

        EntitySourceMappingRequest entitySourceMappingRequest = new EntitySourceMappingRequest();
        entitySourceMappingRequest.setEducationEntity(EXAM);
        EntitySourceMappingData entitySourceMappingData = new EntitySourceMappingData();
        entitySourceMappingData.setEntityId(1l);
        entitySourceMappingRequest
                .setEntitySourceMappingData(Arrays.asList(entitySourceMappingData));

        when(entitySourceMappingRepository.save(any(EntitySourceMappingEntity.class)))
                .thenReturn(entitySourceMappingEntity);
        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.deleteEntitySourceMapping(entitySourceMappingRequest);
        assertNotNull(entitySourceMappingResponse);
        assertEquals(SUCCESS, entitySourceMappingResponse.getStatus());
        assertEquals(1, entitySourceMappingResponse.getData().size());
        assertEquals(entitySourceMappingEntity, entitySourceMappingResponse.getData().get(0));
    }

    @Test
    public void testDeleteEntitySourceMappingFailure() {
        EntitySourceMappingEntity entitySourceMappingEntity = new EntitySourceMappingEntity();
        entitySourceMappingEntity.setActive(true);
        entitySourceMappingEntity.setEntityId(1l);
        entitySourceMappingEntity.setEducationEntity(EXAM);
        entitySourceMappingEntity.setSource(C360);
        when(entitySourceMappingRepository.findByEntityIdAndEducationEntity(anyLong(), anyString()))
                .thenReturn(entitySourceMappingEntity);

        EntitySourceMappingRequest entitySourceMappingRequest = new EntitySourceMappingRequest();
        entitySourceMappingRequest.setEducationEntity(EXAM);
        EntitySourceMappingData entitySourceMappingData = new EntitySourceMappingData();
        entitySourceMappingData.setEntityId(1l);
        entitySourceMappingRequest
                .setEntitySourceMappingData(Arrays.asList(entitySourceMappingData));

        when(entitySourceMappingRepository.save(any(EntitySourceMappingEntity.class)))
                .thenReturn(null);
        EntitySourceMappingResponse entitySourceMappingResponse =
                entitySourceMappingService.deleteEntitySourceMapping(entitySourceMappingRequest);
        assertNotNull(entitySourceMappingResponse);
        assertEquals(FAILED, entitySourceMappingResponse.getStatus());
        assertEquals(EXAM, entitySourceMappingResponse.getEducationEntity());
        assertEquals(new ArrayList<>(), entitySourceMappingResponse.getData());
    }
}
