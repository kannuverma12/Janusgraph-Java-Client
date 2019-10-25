package com.paytm.digital.education.admin.controller;


import com.paytm.digital.education.admin.request.SectionOrderRequest;
import com.paytm.digital.education.admin.response.SectionOrderResponse;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;
import static com.paytm.digital.education.mapping.ErrorEnum.UNAUTHORIZED_REQUEST;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class SectionOrderController {

    private DetailPageSectionHelper detailPageSectionHelper;

    @PutMapping("/admin/v1/sections-order")
    public @ResponseBody SectionOrderResponse updateSectionOrder(@RequestBody @Valid
            SectionOrderRequest sectionOrderRequest,
            @RequestHeader("x-user-id") Long userId) {
        log.info("User : {} going to update sections order : {} .", userId);
        if (Objects.isNull(userId) || userId <= 0) {
            throw new BadRequestException(UNAUTHORIZED_REQUEST,
                    UNAUTHORIZED_REQUEST.getExternalMessage());
        }
        return detailPageSectionHelper.updatePropertyMap(sectionOrderRequest);
    }

    @GetMapping("/admin/v1/sections-order")
    public @ResponseBody SectionOrderResponse getSectionsOrder(@RequestParam @NotEmpty String page,
            @RequestParam @NotEmpty String entity,
            @RequestParam(name = "client", required = false) Client client,
            @RequestHeader("x-user-id") Long userId) {
        log.info("User : {} going to get sections order : {} .", userId, entity);
        if (Objects.isNull(userId) || userId <= 0) {
            throw new BadRequestException(UNAUTHORIZED_REQUEST,
                    UNAUTHORIZED_REQUEST.getExternalMessage());
        }
        return detailPageSectionHelper.getSectionOrder(page, entity, client);
    }
}
