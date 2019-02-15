package com.paytm.digital.education.explore.validators;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_MAX_CHARS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_MIN_CHARS;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AutoSuggestValidator {

    public void validate(String searchTerm) {
        if (StringUtils.isBlank(searchTerm)) {
            throw new RuntimeException("Bad Request. Search Term can't be empty.");
        }

        if (searchTerm.length() < AUTOSUGGEST_MIN_CHARS) {
            throw new RuntimeException(
                    "Search Term must have minimum " + AUTOSUGGEST_MIN_CHARS + " chars.");
        }

        if (searchTerm.length() > AUTOSUGGEST_MAX_CHARS) {
            throw new RuntimeException(
                    "Search Term's maximum length could be " + AUTOSUGGEST_MAX_CHARS + " chars.");
        }

    }
}
