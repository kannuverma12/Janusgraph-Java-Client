package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.AmbassadorRequest;
import com.paytm.digital.education.admin.request.ArticleRequest;
import com.paytm.digital.education.admin.request.EventRequest;
import com.paytm.digital.education.admin.response.CampusAdminResponse;
import com.paytm.digital.education.admin.service.CampusAdminService;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class CampusAdminController {

    private static final Logger log = LoggerFactory.getLogger(CampusAdminController.class);

    private CampusAdminService campusAdminService;

    @PutMapping("/admin/v1/campus-ambassadors")
    public @ResponseBody CampusAdminResponse addAmbassadors(@RequestBody
            @Valid AmbassadorRequest campusAmbassador) {
        log.info("Adding campus ambassador.");
        return campusAdminService.addAmbassadors(campusAmbassador);
    }

    @GetMapping("/admin/v1/campus-ambassadors")
    public @ResponseBody List<XcelCampusAmbassador> getAmbassadors() {
        log.info("Getting all campus ambassadors.");
        return campusAdminService.getAllAmbassadors();
    }

    @PutMapping("/admin/v1/campus-articles")
    public @ResponseBody CampusAdminResponse addArticles(@RequestBody
            @Valid ArticleRequest campusArticle) {
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
            @Valid EventRequest campusEvent) {
        log.info("Adding campus event.");
        return campusAdminService.addEvents(campusEvent);
    }

    @GetMapping("/admin/v1/campus-events")
    public @ResponseBody List<XcelEvent> getEvents() {
        log.info("Getting all campus events.");
        return campusAdminService.getAllEvents();
    }
}
