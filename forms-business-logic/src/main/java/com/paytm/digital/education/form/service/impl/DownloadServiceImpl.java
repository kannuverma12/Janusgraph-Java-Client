package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import com.paytm.digital.education.form.service.DownloadService;
import com.paytm.digital.education.service.TemplateService;
import com.paytm.digital.education.serviceimpl.FreeMarkerTemplateService;
import com.paytm.digital.education.utility.FileUtility;
import com.paytm.digital.education.utility.OpenHtmlToPdfUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

@Data
@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private MongoOperations mongoOperations;
    private TemplateService freeMarkerTemplateService = new FreeMarkerTemplateService();

    protected String getTemplatePath(String type) {
        if (type.equalsIgnoreCase("form")) {
            return "form_template.xhtml";
        } else if (type.equalsIgnoreCase("predictor-invoice")) {
            return "invoice_template_predictor.xhtml";
        } else {
            return "invoice_template.xhtml";
        }
    }

    @Override
    public byte[] getPdfByteArray(FormData model, String type) {
        String merchantId = model.getMerchantId();

        if (merchantId != null) {
            Query query = new Query(Criteria.where("_id").is(merchantId));
            query.fields().include("data");

            MerchantConfiguration merchantConfiguration = mongoOperations.findOne(query, MerchantConfiguration.class);

            if (merchantConfiguration != null) {
                Map<String, Object> data = merchantConfiguration.getData();
                if (data != null && data.containsKey("merchantName")) {
                    model.setMerchantName((String) data.get("merchantName"));
                }
            }
        }

        URI baseUri = FileUtility.getResourcePath(getTemplatePath(type));
        String html = freeMarkerTemplateService.renderTemplate(getTemplatePath(type), model);
        return OpenHtmlToPdfUtility.htmlToPdf(html, baseUri);
    }

    public byte[] getTempAimaResponse(Long orderId, Map<String, Object> templateConfig, String customerId) {

        String url = (String) templateConfig.get("url");
        try (CloseableHttpClient client = HttpClients.createDefault();) {
            HttpPost httpPost = new HttpPost(url);
            String json = "{\"data\":{\"submit\":true}}";
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            for (String key : templateConfig.keySet()) {
                if (!"url".equals(key)) {
                    httpPost.setHeader(key, (String) templateConfig.get(key));
                }
            }

            if (customerId != null) {
                httpPost.setHeader("x-user-id", customerId);
            }

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity httpEntity = response.getEntity();
                String responseString = EntityUtils.toString(httpEntity, "UTF-8");

                if (response.getStatusLine().getStatusCode() == 200) {
                    return Base64.decodeBase64(responseString);
                } else {
                    log.error("Status: {}", response.getStatusLine().getStatusCode());
                    log.error("Headers: {}", response.getAllHeaders());
                    log.error("Body: {}", responseString);
                    return null;
                }
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public FormData getFormDataByUserIdAndOrderId(String userId, Long orderId) {
        try {
            Query query = new Query(Criteria.where("customerId").is(userId));
            query.addCriteria(Criteria.where("formFulfilment.orderId").is(orderId));
            return mongoOperations.findOne(query, FormData.class);
        } catch (Exception e) {
            log.error("ERROR IN FETCHING DATA FROM MONGO DB FOR USERID", e);
            return null;
        }
    }

    @Override
    public FormData getFormDataByMerchantIdAndOrderId(String merchantId, Long orderId) {
        try {
            Query query = new Query(Criteria.where("merchantId").is(merchantId));
            query.addCriteria(Criteria.where("formFulfilment.orderId").is(orderId));
            return mongoOperations.findOne(query, FormData.class);
        } catch (Exception e) {
            log.error("ERROR IN FETCHING DATA FROM MONGO DB FOR MERCHANT", e);
            return null;
        }
    }

    @Override
    public FormData getFormDataByOrderId(Long orderId) {
        try {
            Query query = new Query(Criteria.where("formFulfilment.orderId").is(orderId));
            return mongoOperations.findOne(query, FormData.class);
        } catch (Exception e) {
            log.error("ERROR IN FETCHING DATA FROM MONGO DB FOR MERCHANT", e);
            return null;
        }
    }


}
