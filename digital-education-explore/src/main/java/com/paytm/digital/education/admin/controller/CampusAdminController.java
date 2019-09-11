package com.paytm.digital.education.admin.controller;


import com.paytm.digital.education.admin.response.CampusAdminResponse;
import com.paytm.digital.education.admin.service.CampusAdminService;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class CampusAdminController {

    private CampusAdminService campusAdminService;

    @PutMapping("/admin/v1/campus-ambassadors")
    public @ResponseBody CampusAdminResponse addAmbassadors(@RequestBody
            XcelCampusAmbassador campusAmbassador) {
        log.info("Adding campus ambassador.");
        return campusAdminService.addAmbassadors(campusAmbassador);
    }

    @GetMapping("/admin/v1/campus-ambassadors")
    public @ResponseBody List<XcelCampusAmbassador> getAmbassadors() {
        log.info("Getting all campus ambassadors.");
        return campusAdminService.getAllAmbassadors();
    }

    @PutMapping("/admin/v1/campus-articles")
    public @ResponseBody CampusAdminResponse addAmbassadors(@RequestBody
            XcelArticle campusArticle) {
        log.info("Adding campus article.");
        return campusAdminService.addArticles(campusArticle);
    }

    @GetMapping("/admin/v1/campus-articles")
    public @ResponseBody List<XcelArticle> getArticles() {
        log.info("Getting all campus articles.");
        return campusAdminService.getAllArticles();
    }

    @PutMapping("/admin/v1/campus-events")
    public @ResponseBody CampusAdminResponse addEvent(@RequestBody
            XcelEvent campusEvent) {
        log.info("Adding campus event.");
        return campusAdminService.addEvents(campusEvent);
    }

    @GetMapping("/admin/v1/campus-events")
    public @ResponseBody List<XcelEvent> getEvents() {
        log.info("Getting all campus events.");
        return campusAdminService.getAllEvents();
    }
}
