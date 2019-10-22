package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Section;
import com.paytm.digital.education.explore.response.dto.search.ExamLevelData;
import com.paytm.digital.education.explore.service.PageService;
import com.paytm.digital.education.explore.service.impl.SectionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class SectionsController {

    private PageService pageService;
    private SectionServiceImpl sectionService;

    @GetMapping("/v1/page/{pageName}/sections")
    public List<Section> getPageSections(@PathVariable("pageName") String pageName) {
        return pageService.getPageSections(pageName);
    }

    @GetMapping("/v1/topExamsPerLevel")
    public ExamLevelData getTopExamsPerLevel() {
        return sectionService.getTopExamsPerLevel();
    }
}
