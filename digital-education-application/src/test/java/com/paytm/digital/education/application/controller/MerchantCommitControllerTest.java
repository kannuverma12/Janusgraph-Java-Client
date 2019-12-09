package com.paytm.digital.education.application.controller;

import com.paytm.digital.education.coaching.consumer.controller.PurchaseController;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantOrderData;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyUserInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantNotifyRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.MerchantNotifyResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.MerchantCall;
import com.paytm.digital.education.coaching.enums.MerchantNotifyFailureReason;
import com.paytm.digital.education.coaching.enums.MerchantNotifyStatus;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.MERCHANT_NOTIFY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@TestPropertySource(
        value = "/application-test.properties",
        properties = {
                "kafka.listener.should.configure=true",
                "redis.port=6381",
                "mongo.port=27019",
                "spring.data.mongodb.uri=mongodb://localhost:27019/digital-education"
        }
)
@WebMvcTest(value = PurchaseController.class, secure = false)
public class MerchantCommitControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MerchantCommitControllerTest.class);

    private final Long   userId            = CommonUtils.randomLong();
    private final Long   merchantId        = CommonUtils.randomLong();
    private final String merchantAccessKey = "dsfdsfsd3424324sfdsfsdf";
    private final String orderDate         = "Mon, 04 Nov 2019 09:09:33 GMT";
    private final String merchantSecretKey = "jdhsfks976f7y32iukj3knsafdfs=sdf213=";
    private final String merchantHost      = "http://www.merchant.com";
    private final String merchantMobile    = "9876543210";
    private final String merchantEmail     = "merchant@merchant.com";
    private final String merchantEndPoint  = "/notify";
    private final Long   itemId            = CommonUtils.randomLong();
    private final Long   orderId           = CommonUtils.randomLong();

    @Autowired
    private MockMvc      mockMvc;
    @MockBean
    private MerchantCall merchantCall;

    @Test
    public void allConfigsOK() {
    }

    @Test
    public void merchantCommitSuccess() throws Exception {
        mockMerchantCallSuccess();
        MerchantNotifyRequest request = this.getRequest();

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + MERCHANT_NOTIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void merchantCommitBlankResponse() throws Exception {
        mockMerchantCallEmptyResponse();
        MerchantNotifyRequest request = this.getRequest();

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + MERCHANT_NOTIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void merchantCommitBadRequest() throws Exception {
        mockMerchantCallBadRequest();
        MerchantNotifyRequest request = this.getRequest();

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + MERCHANT_NOTIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void merchantCommitInfraDown() throws Exception {
        mockMerchantCallMerchantInfraDown();
        MerchantNotifyRequest request = this.getRequest();

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + MERCHANT_NOTIFY)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private MerchantNotifyRequest getRequest() {
        MerchantNotifyCartItem cartItem = this.getSuccessCartItem();
        List<MerchantNotifyCartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        NotifyMerchantInfo merchantInfo = this.getSuccessMerchantInfo();
        Map<Long, NotifyMerchantInfo> merchantInfoMap = new HashMap<>();
        merchantInfoMap.put(merchantId, merchantInfo);

        NotifyUserInfo userInfo = this.getSuccessUserInfo();

        MerchantNotifyRequest request = new MerchantNotifyRequest();
        request.setMerchantData(merchantInfoMap);
        request.setOrderCreatedAt(orderDate);
        request.setCartItems(cartItems);
        request.setUserData(userInfo);

        return request;
    }

    private MerchantNotifyCartItem getSuccessCartItem() {
        String referenceId = UUID.randomUUID().toString();

        MerchantNotifyCartItem cartItem = new MerchantNotifyCartItem();
        cartItem.setItemId(itemId);
        cartItem.setOrderId(orderId);
        cartItem.setName("Test");
        cartItem.setSku("12345");
        cartItem.setCustomerId(userId);
        cartItem.setProductId(12345L);
        cartItem.setMerchantId(merchantId);
        cartItem.setVerticalId(17);
        cartItem.setFulfillmentServiceId(102);
        cartItem.setMetaData("{\"reference_id\":\"" + referenceId + "\"}");
        cartItem.setFulfillmentReq("abc");
        cartItem.setPrice(10.0f);
        cartItem.setMrp(10.0f);
        cartItem.setConvFee(0.0f);
        cartItem.setDiscount(0.0f);
        cartItem.setSellingPrice(10.0f);
        cartItem.setQuantity(1);
        cartItem.setReferenceId(referenceId);

        return cartItem;
    }

    private NotifyMerchantInfo getSuccessMerchantInfo() {
        NotifyMerchantInfo merchantInfo = new NotifyMerchantInfo();
        merchantInfo.setMerchantId(merchantId);
        merchantInfo.setMobileNumber(merchantMobile);
        merchantInfo.setEmailId(merchantEmail);
        merchantInfo.setAccessKey(merchantAccessKey);
        merchantInfo.setSecretKey(merchantSecretKey);
        merchantInfo.setHost(merchantHost);
        merchantInfo.setNotifyEndpoint(merchantEndPoint);

        return merchantInfo;
    }

    private NotifyUserInfo getSuccessUserInfo() {
        NotifyUserInfo userInfo = new NotifyUserInfo();
        userInfo.setEmail("user@user.com");
        userInfo.setPhone("8765432190");
        userInfo.setFirstName("Firstname");
        userInfo.setLastName("Lastname");

        return userInfo;
    }

    private void mockMerchantCallSuccess() {
        MerchantNotifyResponse response = MerchantNotifyResponse.builder()
                .status(MerchantNotifyStatus.SUCCESS)
                .failureReason(null)
                .merchantResponse(MerchantOrderData.builder()
                        .merchantReferenceId(UUID.randomUUID().toString())
                        .build())
                .build();
        given(merchantCall.commitMerchantOrder(any(MerchantCommitRequest.class),
                any(NotifyMerchantInfo.class))).willReturn(response);
    }

    private void mockMerchantCallEmptyResponse() {
        MerchantNotifyResponse response = MerchantNotifyResponse.builder()
                .status(MerchantNotifyStatus.PENDING)
                .failureReason(MerchantNotifyFailureReason.BAD_RESPONSE_FROM_MERCHANT_COMMIT)
                .merchantResponse(null)
                .build();
        given(merchantCall.commitMerchantOrder(any(MerchantCommitRequest.class),
                any(NotifyMerchantInfo.class))).willReturn(response);
    }

    private void mockMerchantCallBadRequest() {
        MerchantNotifyResponse response = MerchantNotifyResponse.builder()
                .status(MerchantNotifyStatus.PENDING)
                .failureReason(MerchantNotifyFailureReason.BAD_REQUEST_IN_MERCHANT_COMMIT)
                .merchantResponse(null)
                .build();
        given(merchantCall.commitMerchantOrder(any(MerchantCommitRequest.class),
                any(NotifyMerchantInfo.class))).willReturn(response);
    }

    private void mockMerchantCallMerchantInfraDown() {
        MerchantNotifyResponse response = MerchantNotifyResponse.builder()
                .status(MerchantNotifyStatus.PENDING)
                .failureReason(MerchantNotifyFailureReason.MERCHANT_INFRA_DOWN)
                .merchantResponse(null)
                .build();
        given(merchantCall.commitMerchantOrder(any(MerchantCommitRequest.class),
                any(NotifyMerchantInfo.class))).willReturn(response);
    }
}
