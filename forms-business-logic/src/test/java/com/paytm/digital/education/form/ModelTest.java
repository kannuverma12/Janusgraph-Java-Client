package com.paytm.digital.education.form;

import com.paytm.digital.education.form.request.PaymentPostingRequest;
import com.paytm.digital.education.utility.FileUtility;
import com.paytm.digital.education.utility.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ModelTest {

    @Test
    public void paymentPostingRequest() {

        String request = FileUtility.getResourceFileAsString("payment_posting_request.json");
        PaymentPostingRequest paymentPostingRequest = JsonUtils.fromJson(request, PaymentPostingRequest.class);
        Assert.assertEquals(2, paymentPostingRequest.getPaymentStatus().intValue());
        Assert.assertEquals("719625328049185", paymentPostingRequest.getId());
        Assert.assertEquals(24442, paymentPostingRequest.getCustomerId().intValue());
        Assert.assertEquals("numbershot@yah00.us", paymentPostingRequest.getCustomerEmail());
        Assert.assertEquals("Wyatt", paymentPostingRequest.getCustomerFirstname());
        Assert.assertEquals("Santiago", paymentPostingRequest.getCustomerLastname());
        Assert.assertEquals("9129334090", paymentPostingRequest.getPhoneNumber());
        Assert.assertEquals(
                "fCSBfYfNEczkWnikZjIRSnDuuRAwMfjWYxSvleUZVrZQWvpsoXnG"
                        + "BjevCeVtovsymXiDJcPUZeqPPSguJWdLaLWuXlhPRsjKVYdp",
                paymentPostingRequest.getTitle());


        // real data came from cart in staging environment
        request = FileUtility.getResourceFileAsString("payment_posting_request2.json");
        paymentPostingRequest = JsonUtils.fromJson(request, PaymentPostingRequest.class);
        Assert.assertEquals(2, paymentPostingRequest.getPaymentStatus().intValue());
        Assert.assertEquals("4296686224", paymentPostingRequest.getId());
        Assert.assertEquals(1107204831L, paymentPostingRequest.getCustomerId().intValue());
        Assert.assertEquals("ankit.gupta2109@gmail.com", paymentPostingRequest.getCustomerEmail());
        Assert.assertEquals("Ankit Gupta", paymentPostingRequest.getCustomerFirstname());
        Assert.assertNull(paymentPostingRequest.getCustomerLastname());
        Assert.assertEquals("9899099590", paymentPostingRequest.getPhoneNumber());
        Assert.assertEquals("Fee for AIMA - MAT of  ", paymentPostingRequest.getTitle());
        Assert.assertEquals(new Date(1558785923000L), paymentPostingRequest.getCreatedAt());
        Assert.assertEquals(new Date(1558785923000L), paymentPostingRequest.getUpdatedAt());

    }

}
