package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.factory.DataExportFactory;
import com.paytm.digital.education.admin.factory.DataImportFactory;
import com.paytm.digital.education.admin.validator.ImportDataValidator;
import com.paytm.digital.education.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.ingestion.response.ExportResponse;
import com.paytm.digital.education.ingestion.response.ImportResponse;
import com.paytm.digital.education.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.ingestion.service.importdata.ImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@Api(value = "import/export sheet data", description = "Import/export allowed google sheet data to mongo db.")
@RequestMapping(EDUCATION_BASE_URL)
public class ImportDataController {

    @Autowired
    private ImportDataValidator importDataValidator;

    @Autowired
    private DataExportFactory dataExportFactory;

    @Autowired
    private DataImportFactory dataImportFactory;

    @GetMapping("/v1/import/streams")
    @ApiOperation(value = "Import form's data from google sheet to mongodb")
    public ResponseEntity<ImportResponse> ingestData(
            @RequestHeader("token") String token, @RequestParam("form") @NotEmpty String form) {
        importDataValidator.validateRequest(token);
        final ImportService importService = dataImportFactory.getIngestorService(
                IngestionFormEntity.fromString(form));
        return ResponseEntity.ok(importService.ingest());
    }

    @GetMapping("/v1/export/streams")
    @ApiOperation(value = "Export mongo data to google sheet.")
    public ResponseEntity<ExportResponse> exportData(
            @RequestHeader("token") String token, @RequestParam("form") @NotEmpty String form) {
        importDataValidator.validateRequest(token);
        final ExportService exportService =
                dataExportFactory.getExportService(IngestionFormEntity.fromString(form));
        return ResponseEntity.ok(exportService.export());
    }
}
