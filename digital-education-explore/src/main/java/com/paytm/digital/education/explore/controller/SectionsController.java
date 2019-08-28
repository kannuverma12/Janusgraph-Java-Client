package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.service.PageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class SectionsController {

    private PageService pageService;

    @GetMapping("/v1/page/{pageName}/sections")
    public List<Section> getPageSections(@PathVariable("pageName") String pageName) {
        return pageService.getPageSections(pageName);
    }
}
