package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.database.entity.CampusEvent;
import com.paytm.digital.education.explore.service.impl.ImportAmbassadorServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportArticleServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportEventServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CampusEngagementController {
    private ImportAmbassadorServiceImpl importAmbassadorService;
    private ImportEventServiceImpl importEventService;
    private ImportArticleServiceImpl importArticleService;

    @RequestMapping(method = RequestMethod.POST, path = "/v1/import/campus-ambassadors")
    public @ResponseBody Map<Long, List<CampusEvent>> importCampusAmbassadors()
            throws IOException, GeneralSecurityException, ParseException {
        return importAmbassadorService.importData();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/v1/import/articles")
    public @ResponseBody Map<Long, List<CampusEvent>> importArticles()
            throws IOException, GeneralSecurityException, ParseException {
        return importArticleService.importData();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/v1/import/events")
    public @ResponseBody Map<Long, List<CampusEvent>> importEvents()
            throws IOException, GeneralSecurityException, ParseException {
        return importEventService.importData();
    }
}
