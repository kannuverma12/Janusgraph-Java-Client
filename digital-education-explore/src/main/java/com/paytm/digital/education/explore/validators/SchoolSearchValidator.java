package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.SORT_DISTANCE_FIELD;

@UtilityClass
public class SchoolSearchValidator {

    public boolean isGeoDistanceSortRequest(SearchRequest searchRequest) {
        if (Objects.isNull(searchRequest.getGeoLocation()) || Objects
                .isNull(searchRequest.getGeoLocation().getLat()) || Objects
                .isNull(searchRequest.getGeoLocation().getLon())) {
            return false;
        }

        return !CollectionUtils.isEmpty(searchRequest.getSortOrder()) && searchRequest
                .getSortOrder()
                .containsKey(SORT_DISTANCE_FIELD);

    }
}
