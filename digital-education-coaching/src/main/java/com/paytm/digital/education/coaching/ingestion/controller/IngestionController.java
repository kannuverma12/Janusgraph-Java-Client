package com.paytm.digital.education.coaching.ingestion.controller;

import com.paytm.digital.education.coaching.enums.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(COACHING)
@AllArgsConstructor
public class IngestionController {

    private final IngestorServiceFactory ingestorServiceFactory;

    @GetMapping(V1 + "/ingest")
    public ResponseEntity<IngestorResponse> ingestData(
            @RequestParam("form") @NotEmpty String form) {

        final IngestorService ingestorService = this.ingestorServiceFactory.getIngestorService(
                IngestionFormEntity.fromString(form));

        if (null == ingestorService) {
            return new ResponseEntity("Please try again with correct params",
                    HttpStatus.BAD_REQUEST);
        }

        IngestorResponse ingestorResponse = ingestorService.ingest();

        return ResponseEntity.ok(ingestorResponse);
    }
}
