package com.paytm.digital.education.explore.validators;

import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CourseSearchValidator {

    public void validateRequest(SearchRequest searchRequest) {

        if (!searchRequest.getFilter().containsKey(ENTITY_ID)) {
            throw new BadRequestException(ErrorEnum.ENTITY_ID_IS_MANDATORY,
                    ErrorEnum.ENTITY_ID_IS_MANDATORY.getExternalMessage());
        }

        if (CollectionUtils.isEmpty(searchRequest.getFilter().get(ENTITY_ID))
                || searchRequest.getFilter().get(ENTITY_ID).size() != 1) {
            throw new BadRequestException(ErrorEnum.INVALID_ENTITY_ID_LIST_SIZE,
                    ErrorEnum.INVALID_ENTITY_ID_LIST_SIZE.getExternalMessage());
        }

        if (!(searchRequest.getFilter().get(ENTITY_ID).get(0) instanceof Integer)
                || (int) searchRequest.getFilter().get(ENTITY_ID).get(0) <= 0) {
            throw new BadRequestException(ErrorEnum.INVALID_INSTITUTE_ID,
                    ErrorEnum.INVALID_INSTITUTE_ID.getExternalMessage());
        }

    }
}
