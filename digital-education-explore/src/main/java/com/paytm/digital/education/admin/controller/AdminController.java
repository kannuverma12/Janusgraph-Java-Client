package com.paytm.digital.education.admin.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_UPLOAD_REQUEST;
import static com.paytm.digital.education.mapping.ErrorEnum.UNAUTHORIZED_REQUEST;

import com.paytm.digital.education.admin.response.DocumentUploadResponse;
import com.paytm.digital.education.admin.service.AdminService;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class AdminController {

    private AdminService adminService;

    @PostMapping("/auth/admin/v1/file-upload")
    public List<DocumentUploadResponse> fileUpload(@RequestParam("files") List<MultipartFile> files,
            @RequestParam("entity") String entity,
            @RequestParam("entity-id") Long entityId,
            @RequestParam("relative-url") String relativeUrl,
            @RequestHeader("x-user-id") Long userId) {
        log.info("User : {} going to upload files for entity : {} .", userId, entity);
        if (Objects.isNull(userId) || userId <= 0) {
            throw new BadRequestException(UNAUTHORIZED_REQUEST,
                    UNAUTHORIZED_REQUEST.getExternalMessage());
        }

        if (CollectionUtils.isEmpty(files)) {
            log.error("User : {} requested with no files", userId);
            throw new BadRequestException(INVALID_UPLOAD_REQUEST,
                    INVALID_UPLOAD_REQUEST.getExternalMessage());
        }
        return adminService.uploadDocument(files, entity, relativeUrl, entityId);
    }


}
