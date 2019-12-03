package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;
import com.paytm.digital.education.admin.service.impl.PaytmSourceDataServiceImpl;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaytmSourceDataServiceImplTest {
    @Mock
    private PaytmSourceDataRepository paytmSourceDataRepository;

    @InjectMocks
    private PaytmSourceDataServiceImpl paytmSourceDataService;

    @Test
    public void testInsertPaytmSourceDataSuccessCase() {
        PaytmSourceData paytmSourceData = new PaytmSourceData();
        paytmSourceData.setEntityId(1l);
        paytmSourceData.setData(
                (Map<String, Object>) new HashMap<String, Object>().put("exam_id", 1));

        PaytmSourceData paytmSourceDataExpected = new PaytmSourceData();
        paytmSourceDataExpected.setEntityId(1l);
        paytmSourceDataExpected.setData(
                (Map<String, Object>) new HashMap<String, Object>().put("exam_id", 1));

        PaytmSourceDataRequest paytmSourceDataRequestInput = new PaytmSourceDataRequest();
        paytmSourceDataRequestInput.setEducationEntity(EducationEntity.EXAM);
        paytmSourceDataRequestInput.setPaytmSourceData(Arrays.asList(paytmSourceData));
        when(paytmSourceDataRepository
                .findByEntityIdAndEducationEntityAndSource(anyLong(), anyString(), anyString()))
                .thenReturn(null);
        when(paytmSourceDataRepository.save(any(PaytmSourceData.class)))
                .thenReturn(paytmSourceDataExpected);

        PaytmSourceDataResponse paytmSourceDataResponse =
                paytmSourceDataService.savePaytmSourceData(paytmSourceDataRequestInput);
        assertNotNull(paytmSourceDataResponse);
        assertEquals(paytmSourceDataResponse.getEducationEntity(),
                paytmSourceDataRequestInput.getEducationEntity());
        assertEquals("success", paytmSourceDataResponse.getStatus());
        assertEquals(1, paytmSourceDataResponse.getPaytmSourceData().size());
    }

    @Test
    public void testUpdatePaytmSourceDataSuccessCase() {
        PaytmSourceData paytmSourceData = new PaytmSourceData();
        paytmSourceData.setEntityId(1l);
        Map requestMap = new HashMap<String, Object>();
        requestMap.put("exam_id", 2);
        requestMap.put("exam_name", "test");
        paytmSourceData.setData(requestMap);

        PaytmSourceData paytmSourceDataInDb = new PaytmSourceData();
        paytmSourceData.setEntityId(2l);
        Map map = new HashMap<String, Object>();
        map.put("exam_id", 2);

        PaytmSourceData paytmSourceDataExpected = new PaytmSourceData();
        paytmSourceDataExpected.setEntityId(2l);
        paytmSourceDataExpected.setData(requestMap);

        PaytmSourceDataRequest paytmSourceDataRequestInput = new PaytmSourceDataRequest();
        paytmSourceDataRequestInput.setEducationEntity(EducationEntity.EXAM);
        paytmSourceDataRequestInput.setPaytmSourceData(Arrays.asList(paytmSourceData));
        when(paytmSourceDataRepository
                .findByEntityIdAndEducationEntityAndSource(anyLong(), anyString(), anyString()))
                .thenReturn(paytmSourceDataInDb);
        when(paytmSourceDataRepository.save(any(PaytmSourceData.class)))
                .thenReturn(paytmSourceDataExpected);






        PaytmSourceDataResponse paytmSourceDataResponse =
                paytmSourceDataService.savePaytmSourceData(paytmSourceDataRequestInput);
        assertNotNull(paytmSourceDataResponse);
        assertEquals(paytmSourceDataResponse.getEducationEntity(),
                paytmSourceDataRequestInput.getEducationEntity());
        assertEquals("success", paytmSourceDataResponse.getStatus());
        assertEquals(1, paytmSourceDataResponse.getPaytmSourceData().size());
        assertEquals(paytmSourceData.getEntityId(),
                paytmSourceDataResponse.getPaytmSourceData().get(0).getEntityId());
        assertEquals(requestMap.get("exam_name"),
                paytmSourceDataResponse.getPaytmSourceData().get(0).getData().get("exam_name"));
    }
}
