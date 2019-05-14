package com.paytm.digital.education.form.service;

public interface DownloadService {
    
    byte[] getPdfByteArray(String id, String type);

    byte[] getTempAimaResponse(String id, String type);
}
