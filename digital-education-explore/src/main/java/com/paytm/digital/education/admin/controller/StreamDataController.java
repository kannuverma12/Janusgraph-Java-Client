package com.paytm.digital.education.admin.controller;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.admin.service.impl.StreamDataServiceImpl;
import com.paytm.digital.education.admin.validator.ImportDataValidator;
import com.paytm.digital.education.enums.EducationEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@Api(value = "Update paytm streams in exam/course entity")
@RequestMapping(EDUCATION_BASE_URL)
@RequiredArgsConstructor
public class StreamDataController {

    private final ImportDataValidator importDataValidator;
    private final StreamDataServiceImpl streamDataService;

    @PutMapping("/v1/update/paytm-streams")
    @ApiOperation(value = "Updates paytm stream data in db for education entity provided")
    public ResponseEntity<Object> ingestData(
            @ApiParam(value = "auth token required for authorization", required = true)
            @RequestHeader("token") String token,
            @ApiParam(value = "entity whose stream need to be updated", required = true)
            @RequestParam("entity") @NotBlank EducationEntity educationEntity) {
        importDataValidator.validateRequest(token);
        streamDataService.updatePaytmStream(educationEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
