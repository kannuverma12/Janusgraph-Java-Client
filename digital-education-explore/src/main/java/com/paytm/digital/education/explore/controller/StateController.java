package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.State;
import com.paytm.digital.education.explore.service.StateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class StateController {

    private StateService stateService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/states")
    public Iterable<State> getStates() {
        return stateService.getAllStates();
    }
}
