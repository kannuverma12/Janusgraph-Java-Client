package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.UserAction;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.service.impl.AutoSuggestServiceImpl;
import com.paytm.digital.education.explore.validators.AutoSuggestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping(EDUCATION_BASE_URL)
@Validated
public class AutoSuggestController {

    private AutoSuggestServiceImpl autoSuggestServiceImpl;
    private AutoSuggestValidator   autoSuggestValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/autosuggest")
    public @ResponseBody AutoSuggestResponse autosuggest(@RequestParam("query") String query,
            @RequestParam(value = "entities") List<EducationEntity> entities,
            @RequestParam(value = "actions", required = false) List<UserAction> actions,
            @RequestHeader(value = "x-user-id", required = false) Long userId) {
        log.info("Received autosuggest request for entities :{}", entities.toString());
        autoSuggestValidator.validate(query);
        return autoSuggestServiceImpl.getSuggestions(query, entities, actions, userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "v1/autosuggest/getAll")
    public @ResponseBody AutoSuggestResponse getAllSuggestions(
            @RequestParam("size") @Min(1) Integer size,
            @RequestParam(value = "entity") @NotNull EducationEntity entity) {
        log.info("Received v1/autosuggest/getAll for entity :{}", entity.toString());
        return autoSuggestServiceImpl.getAll(Arrays.asList(entity), true, size);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/autosuggest")
    public @ResponseBody AutoSuggestResponse autosuggestCompare(@RequestParam("query") String query,
            @RequestParam(value = "entities") List<EducationEntity> entities,
            @RequestParam(value = "actions", required = false) List<UserAction> actions,
            @RequestHeader(value = "x-user-id", required = false) Long userId) {
        log.info("Received autosuggest request for entities :{}", entities.toString());
        autoSuggestValidator.validate(query);
        return autoSuggestServiceImpl.getSuggestions(query, entities, actions, userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/institute/autosuggest")
    public @ResponseBody AutoSuggestResponse autosuggestInstitute(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) @Min(2) Integer limit) {
        log.info("Received autosuggest request for institutes ");
        return autoSuggestServiceImpl.autosuggestInstitute(query, limit);
    }
}
