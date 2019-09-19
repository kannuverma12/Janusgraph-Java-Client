package com.paytm.digital.education.coaching.ingestion.controller;

import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportServiceFactory;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(COACHING)
public class IngestionController {

    @Value("${coaching.ingestion.auth.token}")
    private String coachingIngestionAuthToken;

    @Autowired
    private ImportServiceFactory importServiceFactory;
    @Autowired
    private ExportServiceFactory exportServiceFactory;

    @GetMapping(V1 + "/import")
    public ResponseEntity<ImportResponse> ingestData(
            @RequestHeader("token") String token,
            @RequestParam("form") @NotEmpty String form) {

        if (!this.coachingIngestionAuthToken.equals(token)) {
            return new ResponseEntity("Please try again with correct token",
                    HttpStatus.UNAUTHORIZED);
        }

        final ImportService importService = this.importServiceFactory.getIngestorService(
                IngestionFormEntity.fromString(form));
        if (null == importService) {
            return new ResponseEntity("Please try again with correct params",
                    HttpStatus.BAD_REQUEST);
        }
        final ImportResponse importResponse = importService.ingest();
        return ResponseEntity.ok(importResponse);
    }

    @GetMapping(V1 + "/export")
    public ResponseEntity<ExportResponse> exportData(
            //            @RequestHeader("token") String token,
            @RequestParam("form") @NotEmpty String form) {

        //        if (!this.coachingIngestionAuthToken.equals(token)) {
        //            return new ResponseEntity("Please try again with correct token",
        //                    HttpStatus.UNAUTHORIZED);
        //        }

        final ExportService exportService = this.exportServiceFactory.getExportService(
                IngestionFormEntity.fromString(form));
        if (null == exportService) {
            return new ResponseEntity("Please try again with correct params",
                    HttpStatus.BAD_REQUEST);
        }

        final ExportResponse exportResponse = exportService.export();
        return ResponseEntity.ok(exportResponse);
    }
}
