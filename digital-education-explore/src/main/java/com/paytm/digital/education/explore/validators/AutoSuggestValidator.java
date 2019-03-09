package com.paytm.digital.education.explore.validators;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_MAX_CHARS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_MIN_CHARS;

import com.paytm.digital.education.exception.BadAutoSuggestException;
import com.paytm.digital.education.mapping.ErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AutoSuggestValidator {

    public void validate(String searchTerm) {
        if (StringUtils.isBlank(searchTerm)) {
            throw new BadAutoSuggestException(
                ErrorEnum.BAD_AUTO_SUGGEST_QUERY_ERROR, ErrorEnum.BAD_AUTO_SUGGEST_QUERY_ERROR.getExternalMessage());
        }

        if (searchTerm.length() < AUTOSUGGEST_MIN_CHARS) {
            ErrorEnum errorEnum = ErrorEnum.BAD_AUTO_SUGGEST_QUERY_ERROR;
            throw new BadAutoSuggestException(
                            ErrorEnum.MIN_LENGTH_ERROR,
                            ErrorEnum.MIN_LENGTH_ERROR.getExternalMessage(),
                            new Object[]{AUTOSUGGEST_MIN_CHARS});
        }

        if (searchTerm.length() > AUTOSUGGEST_MAX_CHARS) {
            throw new BadAutoSuggestException(
                            ErrorEnum.MAX_LENGTH_ERROR,
                            ErrorEnum.MAX_LENGTH_ERROR.getExternalMessage(),
                            new Object[]{AUTOSUGGEST_MAX_CHARS});
        }

    }
}
