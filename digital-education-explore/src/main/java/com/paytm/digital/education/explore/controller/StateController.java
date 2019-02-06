package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.State;
import com.paytm.digital.education.explore.service.StateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class StateController {

    private StateService stateService;

    @RequestMapping(method = RequestMethod.GET, path = "/states")
    public Iterable<State> getStates() {
        return stateService.getAllStates();
    }
}
