package com.paytm.digital.education.form.handler;

import com.paytm.digital.education.form.aws.S3FileService;
import com.paytm.digital.education.form.config.AuthorizationService;
import com.paytm.digital.education.form.model.DownloadOrder;
import com.paytm.digital.education.form.service.DownloadService;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DownloadOrderHandler extends BaseHandler<DownloadOrder> {

    private final DownloadService downloadService;

    private final String awsPath;

    private final String fulFillmentURL;

    private final S3FileService s3FileService;

    private final String orderFilePath = "/education/forms/orders/";

    private final AuthorizationService authService;

    public DownloadOrderHandler(
            final DownloadService downloadService,
            final S3FileService s3FileService,
            @Value("AWS_PATH") String awsPath,
            @Value("FULFILLMENT_URL") String fulFillmentURL,
            final AuthorizationService authService) {
        this.downloadService = downloadService;
        this.awsPath = awsPath;
        this.fulFillmentURL = fulFillmentURL;
        this.s3FileService = s3FileService;
        this.authService = authService;
    }

    @Override
    public void handle(DownloadOrder order) throws UnsupportedEncodingException {
        byte[] contents = null;
        // if (authService.getMerchantId() == null) {
        // } else {
        // }
        // todo: make this generic based on merchant config
        // byte[] pdfBytes = downloadService.getPdfByteArray(order.getOrderId(), "");
        //byte[] contents = downloadService.getTempAimaResponse(order.getOrderId());
        InputStream is = new ByteArrayInputStream(contents);
        String fileName = order.getOrderId() + order.getUserId() + ".pdf";
        s3FileService.put(is, orderFilePath + fileName);

        HttpPost postRequest = new HttpPost(fulFillmentURL);

        postRequest.addHeader("Content-Type", "application/json");

        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("name", fileName);
                put("service_id", 5);
                put("user_id", order.getUserId());
                put("job_type", "UPLOAD");
                put("input_file_path", awsPath + "/user/"
                        + order.getUserId() + "/education/forms/orders/reports/pdf/" + fileName);
                put("records_count", "1");
                put("job_status", 3);
            }
        };

        postRequest.setEntity(new StringEntity(JsonUtils.toJson(data)));

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(postRequest);
            httpClient.getConnectionManager().shutdown();
            // close connection

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

}
