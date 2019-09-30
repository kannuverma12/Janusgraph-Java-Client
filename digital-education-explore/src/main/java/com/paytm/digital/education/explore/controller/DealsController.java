package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.service.impl.DealsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class DealsController {

    private DealsServiceImpl dealsService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/deals/select/items")
    public List<String> getSelectItems(@RequestParam("type") String type) {
        return dealsService.getSelectItems(type);
    }
}
