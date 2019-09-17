package com.paytm.digital.education.admin.service;

import com.paytm.digital.education.admin.response.DocumentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    List<DocumentUploadResponse> uploadDocument(List<MultipartFile> files, String type,
            String prefix, Long entityId);

}
