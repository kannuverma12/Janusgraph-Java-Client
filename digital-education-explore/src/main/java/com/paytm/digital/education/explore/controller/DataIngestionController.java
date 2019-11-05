package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.StaticDataIngestionResponse;
import com.paytm.digital.education.explore.service.IngestStaticDataService;
import com.paytm.digital.education.explore.service.impl.ImportIncrementalDataService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;


@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
public class DataIngestionController {

    private static Logger log = LoggerFactory.getLogger(DataIngestionController.class);

    private ImportIncrementalDataService importIncrementalDataService;
    private IngestStaticDataService      importStaticDataService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/data")
    public @ResponseBody boolean importData() throws java.io.FileNotFoundException {
        importIncrementalDataService.importData(null, null, null);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/manual")
    public @ResponseBody boolean importDataManually(@RequestParam("entity") @NotBlank String entity,
            @RequestParam("version") @Min(1) Integer directory,
            @RequestParam("update_version") @NotNull Boolean updateVersion) {
        importIncrementalDataService.importData(entity, directory, updateVersion);
        return true;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/v1/import/catalog")
    public @ResponseBody List<StaticDataIngestionResponse> importCatalogData(
            @RequestBody List<EntityData> entityDataList) {
        log.info("Received request to ingest data from catalog. : {}", entityDataList.toString());
        return importStaticDataService.ingestDataEntityWise(entityDataList);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/v1/import/entity-static-data")
    public @ResponseBody List<StaticDataIngestionResponse> importStaticData(
            @RequestBody List<EntityData> entityDataList) {
        log.info("Received request to ingest data. : {}", entityDataList.toString());
        return importStaticDataService.ingestDataEntityWise(entityDataList);
    }

}
