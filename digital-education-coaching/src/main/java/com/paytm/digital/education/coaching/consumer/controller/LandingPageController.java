package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.database.entity.Section;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;

@RestController
@RequestMapping(COACHING_BASE)
@AllArgsConstructor
public class LandingPageController {

    @GetMapping("/v1/page")
    public List<Section> getPageSections(@RequestParam("pageName") String pageName) {
        return new ArrayList<>();
    }
}
