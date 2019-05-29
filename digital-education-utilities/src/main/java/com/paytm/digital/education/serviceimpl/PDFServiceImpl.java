package com.paytm.digital.education.serviceimpl;

import com.paytm.digital.education.service.PDFService;
import com.paytm.digital.education.utility.FileUtility;
import com.paytm.digital.education.utility.OpenHtmlToPdfUtility;

import java.net.URI;

public class PDFServiceImpl implements PDFService {

    private FreeMarkerTemplateService freeMarkerTemplateService = new FreeMarkerTemplateService();

    public byte[] generatePDFFromTemplate(String template, Object pdfModel) {
        String html = freeMarkerTemplateService.renderTemplate(template, pdfModel);
        if (html == null) {
            return null;
        }

        URI baseUri = FileUtility.getResourcePath(template);
        return OpenHtmlToPdfUtility.htmlToPdf(html, baseUri);
    }
}
