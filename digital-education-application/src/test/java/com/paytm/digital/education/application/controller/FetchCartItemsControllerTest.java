package com.paytm.digital.education.application.controller;

import com.paytm.digital.education.cache.redis.RedisCacheService;
import com.paytm.digital.education.coaching.consumer.controller.CartItemsController;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.request.FetchCartItemsRequestBody;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CartDataResponse;
import com.paytm.digital.education.database.dao.CoachingCourseDAO;
import com.paytm.digital.education.database.dao.CoachingInstituteDAO;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
import com.paytm.digital.education.database.repository.CoachingProgramRepository;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.CachingConstants.CACHE_KEY_DELIMITER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RunWith(SpringRunner.class)
@ActiveProfiles(value = {"test"})
@WebMvcTest(value = CartItemsController.class, secure = false)
public class FetchCartItemsControllerTest {

    private static final Logger log = LoggerFactory.getLogger(FetchCartItemsControllerTest.class);

    private final String merchantData  =
            "{\"product_list\":[{\"description\":\"desc\",\"merchant_product_tax_data\":{\"gstin\":\"abcd\",\"total_cgst\":1,\"total_igst\":1,\"total_sgst\":1,\"total_utgst\":1},\"price\":25378.9,\"product_id\":\"1\",\"product_name\":\"abcd\",\"quantity\":1}]}";
    private final String transactionId = "abc";
    private final Long   merchantId    = 63794147L;
    private final Long   userId        = 123L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisCacheService              redisCacheService;
    @Autowired
    private CoachingInstituteDAO           coachingInstituteDAO;
    @Autowired
    private CoachingCourseDAO              coachingCourseDAO;
    @Autowired
    private CoachingInstituteRepositoryNew coachingInstituteRepositoryNew;
    @Autowired
    private CoachingProgramRepository      coachingProgramRepository;

    @Before
    public void setup() {
    }

    @After
    public void clear() {
    }

    @Test
    public void successResponse() throws Exception {
        CoachingCourseEntity coachingCourseEntity=createCoachingCourse();
        CoachingInstituteEntity coachingInstituteEntity=createCoachingInstitute();
        FetchCartItemsRequestBody request = getRequest();
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + "/fetch-cart-items")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        CartDataResponse cartDataResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), CartDataResponse.class);
        CheckoutCartItem checkoutCartItem=cartDataResponse.getCartItems().get(0);
        Assert.assertEquals((long) checkoutCartItem.getProductId(), 123L);
        String referenceId=checkoutCartItem.getReferenceId();
        String cacheKey=getCacheKey(referenceId,123L,"1");
        String value=redisCacheService.getValueFromCache(cacheKey);
        Assert.assertNotNull(value);
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
        redisCacheService.delKeyFromCache(cacheKey);
    }

    @Test
    public void invalidMerchantId() throws Exception {
        CoachingCourseEntity coachingCourseEntity=createCoachingCourse();
        CoachingInstituteEntity coachingInstituteEntity=createCoachingInstitute();
        FetchCartItemsRequestBody request = getRequest();
        request.setMerchantId(123L);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + "/fetch-cart-items")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }


    @Test
    public void invalidMerchantProductId() throws Exception {
        CoachingCourseEntity coachingCourseEntity=createCoachingCourse();
        CoachingInstituteEntity coachingInstituteEntity=createCoachingInstitute();
        FetchCartItemsRequestBody request = getRequest();
        request.setMerchantData("{\"product_list\":[{\"description\":\"desc\",\"merchant_product_tax_data\":{\"gstin\":\"abcd\",\"total_cgst\":1,\"total_igst\":1,\"total_sgst\":1,\"total_utgst\":1},\"price\":25378.9,\"product_id\":\"123\",\"product_name\":\"abcd\",\"quantity\":1}]}");
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + "/fetch-cart-items")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }


    @Test
    public void invalidMerchantData() throws Exception {
        CoachingCourseEntity coachingCourseEntity=createCoachingCourse();
        CoachingInstituteEntity coachingInstituteEntity=createCoachingInstitute();
        FetchCartItemsRequestBody request = getRequest();
        request.setMerchantData("{\"product_list\":[\"description\":\"desc\",\"merchant_product_tax_data\":{\"gstin\":\"abcd\",\"total_cgst\":1,\"total_igst\":1,\"total_sgst\":1,\"total_utgst\":1},\"price\":25378.9,\"product_id\":\"123\",\"product_name\":\"abcd\",\"quantity\":1}]}");
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + "/fetch-cart-items")
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }

    private FetchCartItemsRequestBody getRequest(){
        return FetchCartItemsRequestBody.builder()
                .merchantData(merchantData)
                .merchantId(merchantId)
                .transactionId(transactionId)
                .userId(userId).build();
    }

    private String getCacheKey(String referenceId, Long paytmProductId,
            String merchantProductId) {
        return referenceId + CACHE_KEY_DELIMITER + merchantProductId
                + CACHE_KEY_DELIMITER + String.valueOf(paytmProductId);
    }

    private CoachingCourseEntity createCoachingCourse() {
        CoachingCourseEntity coachingCourseEntity =
                new CoachingCourseEntity();
        coachingCourseEntity.setPaytmProductId(123L);
        coachingCourseEntity.setMerchantProductId("1");
        coachingCourseEntity.setIsEnabled(true);
        coachingCourseEntity.setCourseId(100000L);
        coachingCourseEntity.setCourseType(CourseType.CLASSROOM_COURSE);
        coachingCourseEntity.setIsDynamic(true);
        coachingCourseEntity.setCoachingInstituteId(123L);
        coachingCourseEntity.setOriginalPrice(123D);
        coachingCourseEntity.setDiscountedPrice(123D);
        return coachingProgramRepository.save(coachingCourseEntity);
    }

    private CoachingInstituteEntity createCoachingInstitute() {
        CoachingInstituteEntity
                coachingInstituteEntity = new CoachingInstituteEntity();
        coachingInstituteEntity.setInstituteId(123L);
        coachingInstituteEntity.setIsEnabled(true);
        coachingInstituteEntity.setPaytmMerchantId("63794147");
        return coachingInstituteRepositoryNew.save(coachingInstituteEntity);
    }

}
