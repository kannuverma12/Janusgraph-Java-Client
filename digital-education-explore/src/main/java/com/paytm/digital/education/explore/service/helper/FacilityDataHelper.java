package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LOGO;

import com.paytm.digital.education.explore.response.dto.detail.Facility;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FacilityDataHelper {

    private PropertyReader propertyReader;

    @Cacheable(value = "properties", key = "#instituteId", unless = "#result == null")
    public List<Facility> getFacilitiesData(long instituteId, List<String> facilites) {
        if (!CollectionUtils.isEmpty(facilites)) {
            Map<String, Map<String, Object>> propertyMap =
                    propertyReader.getPropertiesAsMap(facilites, EXPLORE_COMPONENT, FACILITIES);
            List<Facility> facilityList = new ArrayList<>();
            for (String facilityKey : facilites) {
                Facility facilityData = new Facility();
                if (!CollectionUtils.isEmpty(propertyMap) && propertyMap.containsKey(facilityKey)
                        && propertyMap.get(facilityKey).containsKey(DISPLAY_NAME)) {
                    facilityData.setName(propertyMap.get(facilityKey).get(DISPLAY_NAME).toString());
                    facilityData.setLogoUrl(propertyMap.get(facilityKey).get(LOGO).toString());
                    facilityList.add(facilityData);
                }
            }
            return facilityList;
        }
        return null;
    }
}
