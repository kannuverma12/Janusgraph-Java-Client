package com.paytm.digital.education.application.controller;

import com.paytm.digital.education.coaching.consumer.controller.CheckoutController;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutMetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.CheckoutDataRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CheckoutDataResponse;
import com.paytm.digital.education.coaching.utils.ComparisonUtils;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_VERTICAL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.DPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.SPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_CGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_IGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_SGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_UTGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.CHECKOUT_DATA;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RunWith(SpringRunner.class)
@TestPropertySource(
        value = "/application-test.properties",
        properties = {
                "kafka.listener.should.configure=true",
                "kafka.listener.endpoint.enabled=true",
                "redis.port=6383",
                "mongo.port=27022",
                "spring.data.mongodb.uri=mongodb://localhost:27022/digital-education",
                "spring.kafka.bootstrap-servers=localhost:9096"
        }
)
@WebMvcTest(value = CheckoutController.class, secure = false)
public class CheckoutControllerTest {

    private static final Logger log = LoggerFactory.getLogger(FetchCartItemsControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {

    }

    @After
    public void clear() {

    }

    @Test
    public void successResponse() throws Exception {
        CheckoutDataRequest request = getCheckoutDataRequest();
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + CHECKOUT_DATA)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        CheckoutDataResponse checkoutDataResponse
                = JsonUtils
                .fromJson(result.getResponse().getContentAsString(), CheckoutDataResponse.class);
        CartItem cartItem = checkoutDataResponse.getCartItems().get(0);
        Assert.assertNotNull(cartItem);
        Assert.assertNotNull(cartItem.getMetaData());
        Assert.assertNotNull(cartItem.getMetaData().getTcs());
        Assert.assertTrue(ComparisonUtils.thresholdBasedFloatsComparison(
                request.getCartItems().get(0).getBasePrice() * 1000F,
                cartItem.getMetaData().getTcs().getBasePrice()));
        Assert.assertEquals(cartItem.getMetaData().getTcs().getCgst(), TCS_CGST_PERCANTAGE);
        Assert.assertEquals(cartItem.getMetaData().getTcs().getSgst(), TCS_SGST_PERCANTAGE);
        Assert.assertEquals(cartItem.getMetaData().getTcs().getUtgst(), TCS_UTGST_PERCANTAGE);
        Assert.assertEquals(cartItem.getMetaData().getTcs().getIgst(), TCS_IGST_PERCANTAGE);
        Assert.assertEquals(cartItem.getMetaData().getTcs().getSpin(), SPIN);
        Assert.assertEquals(cartItem.getMetaData().getTcs().getDpin(), DPIN);
        Assert.assertNull(cartItem.getMetaData().getTcs().getAgstin());
        Assert.assertNull(cartItem.getMetaData().getTcs().getHsn());
        Assert.assertNull(cartItem.getMetaData().getTcs().getCpin());
        Assert.assertNull(cartItem.getMetaData().getTcs().getCgstin());
    }

    @Test
    public void invalidCartItems() throws Exception {
        CheckoutDataRequest request = getCheckoutDataRequest();
        request.setCartItems(null);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + CHECKOUT_DATA)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    public void invalidMetaData() throws Exception {
        CheckoutDataRequest request = getCheckoutDataRequest();
        request.getCartItems().get(0).setMetaData(null);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(COACHING_BASE + V1 + CHECKOUT_DATA)
                        .contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(request))
                        .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    private CheckoutDataRequest getCheckoutDataRequest() {
        CheckoutCartItem cartItem = new CheckoutCartItem();
        ConvTaxInfo convTaxInfo =
                ConvTaxInfo.builder()
                        .totalCGST(0F)
                        .totalIGST(0F)
                        .totalSGST(0F)
                        .totalUTGST(0F)
                        .build();
        TaxInfo taxInfo =
                TaxInfo.builder()
                        .totalCGST(0F)
                        .totalIGST(0F)
                        .totalSGST(0F)
                        .totalUTGST(0F)
                        .build();
        CheckoutMetaData metaData = CheckoutMetaData.builder()
                .courseId(200L)
                .courseType(CourseType.CLASSROOM_COURSE.getText())
                .taxInfo(taxInfo)
                .convTaxInfo(convTaxInfo)
                .merchantProductId("1")
                .build();
        cartItem.setBasePrice(25378.9F * 2);
        cartItem.setQuantity(2);
        cartItem.setConvFee(0F);
        cartItem.setTotalSellingPrice(25378.9F * 2);
        cartItem.setUnitSellingPrice(25378.9F);
        cartItem.setProductId(123L);
        cartItem.setCategoryId("165018");
        cartItem.setEducationVertical(COACHING_VERTICAL_NAME);
        cartItem.setMetaData(metaData);
        cartItem.setReferenceId("abc");

        CheckoutDataRequest checkoutDataRequest = new CheckoutDataRequest();
        List<CheckoutCartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        checkoutDataRequest.setCartItems(cartItems);
        return checkoutDataRequest;
    }
}
