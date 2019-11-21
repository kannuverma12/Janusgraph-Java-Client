package com.paytm.digital.education.application.controller;

import com.paytm.digital.education.cache.redis.RedisCacheService;
import com.paytm.digital.education.coaching.consumer.controller.PurchaseController;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.TCS;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.FetchCartItemsRequestBody;
import com.paytm.digital.education.coaching.consumer.model.request.VerifyRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CartDataResponse;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.VerifyResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.MerchantProductsTransformerService;
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

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_VERTICAL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CachingConstants.CACHE_KEY_DELIMITER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.DPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.SAC;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.SPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_CGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_IGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_SGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_UTGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.VERIFY;

@RunWith(SpringRunner.class)
@ActiveProfiles(value = {"test"})
@WebMvcTest(value = PurchaseController.class, secure = false)
public class VerifyCartItemControllerTest {

    private static final Logger log = LoggerFactory.getLogger(VerifyCartItemControllerTest.class);

    private final String merchantData  =
            "{\"product_list\":[{\"description\":\"desc\",\"merchant_product_tax_data\":{\"gstin\":\"abcd\",\"total_cgst\":0,\"total_igst\":0,\"total_sgst\":0,\"total_utgst\":0},\"price\":25378.9,\"product_id\":\"1\",\"product_name\":\"abcd\",\"quantity\":1}]}";
    private final String transactionId = "abc";
    private final Long   merchantId    = 63794147L;
    private final Long   userId        = 123L;

    @Autowired
    private MockMvc                            mockMvc;
    @Autowired
    private CoachingInstituteRepositoryNew     coachingInstituteRepositoryNew;
    @Autowired
    private CoachingProgramRepository          coachingProgramRepository;
    @Autowired
    private MerchantProductsTransformerService merchantProductsTransformerService;
    @Autowired
    private RedisCacheService                  redisCacheService;

    @Before
    public void setup() {
    }

    @After
    public void clear() {
    }

    @Test
    public void verifySuccessResponseForDynamicCourse() throws Exception {
        CoachingInstituteEntity coachingInstituteEntity=createInstituteEntity();
        CoachingCourseEntity coachingCourseEntity=createDynamicCoachingCourseEntity();
        CartDataResponse cartDataResponse = merchantProductsTransformerService
                .fetchCartDataFromVertical(getFetchCartItemRequest());
        CheckoutCartItem checkoutCartItem=cartDataResponse.getCartItems().get(0);
        VerifyRequest request = getVerifyRequest();
        request.getCartItems().get(0).setReferenceId(checkoutCartItem.getReferenceId());
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + VERIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        VerifyResponse verifyResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), VerifyResponse.class);
        Assert.assertNotNull(verifyResponse);
        Assert.assertEquals(verifyResponse.getAcknowledged(),true);
        String cacheKey=getCacheKey(checkoutCartItem.getReferenceId(),123L,"1");
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
        redisCacheService.delKeyFromCache(cacheKey);
    }

    @Test
    public void verifySuccessResponseForNonDynamicCourse() throws Exception {
        CoachingInstituteEntity coachingInstituteEntity=createInstituteEntity();
        CoachingCourseEntity coachingCourseEntity=createStaticCoachingCourseEntity();
        VerifyRequest request = getVerifyRequest();
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + VERIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        VerifyResponse verifyResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), VerifyResponse.class);
        Assert.assertNotNull(verifyResponse);
        Assert.assertEquals(verifyResponse.getAcknowledged(),true);
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }

    @Test
    public void failureResponsePriceChange() throws Exception {
        CoachingInstituteEntity coachingInstituteEntity=createInstituteEntity();
        CoachingCourseEntity coachingCourseEntity=createStaticCoachingCourseEntity();
        VerifyRequest request = getVerifyRequest();
        request.getCartItems().get(0).setSellingPrice(123F);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + VERIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        VerifyResponse verifyResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), VerifyResponse.class);
        Assert.assertNotNull(verifyResponse);
        Assert.assertEquals(verifyResponse.getAcknowledged(),false);
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }

    @Test
    public void failureResponseInvalidCourseId() throws Exception {
        CoachingInstituteEntity coachingInstituteEntity=createInstituteEntity();
        CoachingCourseEntity coachingCourseEntity=createStaticCoachingCourseEntity();
        VerifyRequest request = getVerifyRequest();
        request.getCartItems().get(0).getMetaData().setCourseId(123L);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + VERIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        VerifyResponse verifyResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), VerifyResponse.class);
        Assert.assertNotNull(verifyResponse);
        Assert.assertEquals(verifyResponse.getAcknowledged(),false);
        coachingInstituteRepositoryNew.delete(coachingInstituteEntity);
        coachingProgramRepository.delete(coachingCourseEntity);
    }

    private CoachingInstituteEntity createInstituteEntity() {
        CoachingInstituteEntity
                coachingInstituteEntity = new CoachingInstituteEntity();
        coachingInstituteEntity.setInstituteId(123L);
        coachingInstituteEntity.setIsEnabled(true);
        coachingInstituteEntity.setPaytmMerchantId("63794147");
        return coachingInstituteRepositoryNew.save(coachingInstituteEntity);
    }

    private CoachingCourseEntity createDynamicCoachingCourseEntity() {
        CoachingCourseEntity coachingCourseEntity =
                new CoachingCourseEntity();
        coachingCourseEntity.setPaytmProductId(123L);
        coachingCourseEntity.setMerchantProductId("1");
        coachingCourseEntity.setIsEnabled(true);
        coachingCourseEntity.setCourseId(200L);
        coachingCourseEntity.setCourseType(CourseType.CLASSROOM_COURSE);
        coachingCourseEntity.setIsDynamic(true);
        coachingCourseEntity.setCoachingInstituteId(123L);
        coachingCourseEntity.setOriginalPrice(25378.9D);
        coachingCourseEntity.setDiscountedPrice(25378.9D);
        return coachingProgramRepository.save(coachingCourseEntity);
    }

    private CoachingCourseEntity createStaticCoachingCourseEntity() {
        CoachingCourseEntity coachingCourseEntity =
                new CoachingCourseEntity();
        coachingCourseEntity.setPaytmProductId(123L);
        coachingCourseEntity.setMerchantProductId("1");
        coachingCourseEntity.setIsEnabled(true);
        coachingCourseEntity.setCourseId(200L);
        coachingCourseEntity.setCourseType(CourseType.CLASSROOM_COURSE);
        coachingCourseEntity.setIsDynamic(false);
        coachingCourseEntity.setCoachingInstituteId(123L);
        coachingCourseEntity.setOriginalPrice(25378.9D);
        coachingCourseEntity.setDiscountedPrice(25378.9D);
        return coachingProgramRepository.save(coachingCourseEntity);
    }
    private VerifyRequest getVerifyRequest(){
        CartItem cartItem=new CartItem();
        ConvTaxInfo convTaxInfo=
                ConvTaxInfo.builder()
                        .totalCGST(0F)
                        .totalIGST(0F)
                        .totalSGST(0F)
                        .totalUTGST(0F)
                        .build();
        TaxInfo taxInfo=
                TaxInfo.builder()
                        .totalCGST(0F)
                        .totalIGST(0F)
                        .totalSGST(0F)
                        .totalUTGST(0F)
                        .build();
        TCS tcs = TCS
                .builder()
                .basePrice(25378.9F * 1000F)
                .igst(TCS_IGST_PERCANTAGE)
                .cgst(TCS_CGST_PERCANTAGE)
                .sgst(TCS_SGST_PERCANTAGE)
                .utgst(TCS_UTGST_PERCANTAGE)
                .spin(SPIN)
                .dpin(DPIN)
                .sac(SAC)
                .agstin(null)
                .hsn(null)
                .cpin(null)
                .cgstin(null)
                .build();

        MetaData metaData=MetaData.builder()
                .courseId(200L)
                .courseType(CourseType.CLASSROOM_COURSE.getText())
                .taxInfo(taxInfo)
                .convTaxInfo(convTaxInfo)
                .merchantProductId("1")
                .tcs(tcs)
                .build();
        cartItem.setBasePrice(25378.9F);
        cartItem.setQuantity(1);
        cartItem.setConvFee(0F);
        cartItem.setSellingPrice(25378.9F);
        cartItem.setProductId(123L);
        cartItem.setCategoryId("165018");
        cartItem.setEducationVertical(COACHING_VERTICAL_NAME);
        cartItem.setMetaData(metaData);
        cartItem.setReferenceId("abc");

        VerifyRequest verifyRequest=new VerifyRequest();
        List<CartItem> cartItems=new ArrayList<>();
        cartItems.add(cartItem);
        verifyRequest.setCartItems(cartItems);
        return verifyRequest;
    }

    private FetchCartItemsRequestBody getFetchCartItemRequest(){
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
}
