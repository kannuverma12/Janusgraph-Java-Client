package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.UserAction;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.service.impl.AutoSuggestServiceImpl;
import com.paytm.digital.education.explore.validators.AutoSuggestValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Controller
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class AutoSuggestController {

    private AutoSuggestServiceImpl autoSuggestServiceImpl;
    private AutoSuggestValidator autoSuggestValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/autosuggest")
    public @ResponseBody AutoSuggestResponse autosuggest(@RequestParam("query") String query,
            @RequestParam(value = "entities") List<EducationEntity> entities,
            @RequestParam(value = "actions", required = false) List<UserAction> actions,
            @RequestHeader(value = "x-user-id", required = false) Long userId) {

        autoSuggestValidator.validate(query);

        return autoSuggestServiceImpl.getSuggestions(query, entities, actions, userId);
    }
}
