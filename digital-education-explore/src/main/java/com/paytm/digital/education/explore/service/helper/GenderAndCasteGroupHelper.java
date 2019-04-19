package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.explore.constants.ExploreConstants.CASTEGROUP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GENDER;

@Service
@AllArgsConstructor

public class GenderAndCasteGroupHelper {
    private PropertyReader propertyReader;

    @Cacheable(value = "gender_category_map")
    public Map<String, Map<String, Object>> getGenderAndCasteGroupMap() {
        List<String> keys = Arrays.asList(GENDER, CASTEGROUP);
        Map<String, Map<String, Object>> genderCasteDataMap = propertyReader
                .getPropertiesAsMap(keys, EXPLORE_COMPONENT,
                        CUTOFF);

        return genderCasteDataMap;
    }
}
