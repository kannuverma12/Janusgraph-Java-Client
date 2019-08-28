package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.LANDING_PAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(value = COACHING)
public class LandingPageController {

    @Autowired
    private PageService pageService;

    @GetMapping(V1 + LANDING_PAGE)
    public List<Section> getPageSections(@RequestParam("pageName") final String pageName) {
        return this.pageService.getPageSections(pageName);
    }
}
