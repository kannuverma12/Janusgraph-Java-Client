package com.paytm.digital.education.admin.controller;


import com.paytm.digital.education.admin.request.SectionOrderRequest;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class SectionOrderController {

    private DetailPageSectionHelper detailPageSectionHelper;

    @PutMapping("/admin/v1/sections-order")
    public @ResponseBody List<String> updateSectionOrder(@RequestBody @Valid
            SectionOrderRequest sectionOrderRequest) {
        log.info("Update sections Order");
        return detailPageSectionHelper.updatePropertyMap(sectionOrderRequest);
    }

    @GetMapping("/admin/v1/sections-order")
    public @ResponseBody List<String> getSectionsOrder(@RequestParam @NotEmpty String entity,
            @RequestParam(name = "client", required = false) Client client) {
        log.info("Getting section order.");
        if (Objects.nonNull(detailPageSectionHelper.getSectionOrder(entity, client))) {
            return detailPageSectionHelper.getSectionOrder(entity, client);
        }
        return Collections.emptyList();

    }
}
