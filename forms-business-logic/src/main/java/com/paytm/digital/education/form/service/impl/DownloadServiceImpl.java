package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.service.DownloadService;
import com.paytm.digital.education.service.TemplateService;
import com.paytm.digital.education.serviceimpl.FreeMarkerTemplateService;
import com.paytm.digital.education.utility.FileUtility;
import com.paytm.digital.education.utility.OpenHtmlToPdfUtility;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.net.URI;

@Data
@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private MongoOperations mongoOperations;
    private TemplateService freeMarkerTemplateService = new FreeMarkerTemplateService();

    protected String getTemplatePath(String type) {
        if (type.equalsIgnoreCase("form")) {
            return "form_template.xhtml";
        } else {
            return "invoice_template.xhtml";
        }
    }

    protected byte[] getPdf(Object model, String type) {
        String html = freeMarkerTemplateService.renderTemplate(getTemplatePath(type), model);
        URI baseUri = FileUtility.getResourcePath(getTemplatePath(type));
        return OpenHtmlToPdfUtility.htmlToPdf(html, baseUri);
    }

    @Override
    public byte[] getPdfByteArray(String id, String type) {
        Query query = new Query(Criteria.where("formFulfilment.orderId").is(id));
        FormData df = mongoOperations.findOne(query, FormData.class);
        return getPdf(df, type);
    }

    public byte[] getTempAimaResponse(String id, String type) {
        // parse url fetched
        String url = "http://matapi.aima.in/api/web/v1/print-registration-form";

        HttpPost postRequest = new HttpPost(url);

        // set headers
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", "Bearer u-cWbiC0s21PH0qhDhtMO6R_j4lhNDXm_1553088165");
        postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        postRequest.addHeader("x-api-token", "afLXGmUj6SAU8rSO-4SZoHAk0jNuIVaD");
        postRequest.addHeader("x-api-version", "1.0");

        // execute post call
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();
            byte[] b = EntityUtils.toByteArray(httpEntity);
            httpClient.getConnectionManager().shutdown();
            return b;
            // close connection

        } catch (Exception e) {
            return null;
        }
    }
}
