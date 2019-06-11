package com.paytm.digital.education.service;

public interface PDFService {

    public byte[] generatePDFFromTemplate(String template, Object pdfModel);

}
