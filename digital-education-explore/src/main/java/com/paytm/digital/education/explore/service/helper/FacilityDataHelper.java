package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.explore.response.dto.detail.Facility;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacilityResponse;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.constant.ExploreConstants.FACILITIES_MASTER_LIST;
import static com.paytm.digital.education.constant.ExploreConstants.FACILITIES_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.SCHOOL_FACILITY_KEY;

@Service
@AllArgsConstructor
public class FacilityDataHelper {

    private PropertyReader propertyReader;

    @EduCache(cache = "properties", shouldCacheNull = false)
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
                    facilityData.setLogoUrl(CommonUtil.getAbsoluteUrl(
                            propertyMap.get(facilityKey).get(LOGO).toString(), FACILITIES));
                    facilityList.add(facilityData);
                }
            }
            return facilityList;
        }
        return null;
    }

    public Map<String, String> getFacilitiesMasterList() {
        Map<String, Object> facilityDataMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, FACILITIES_MASTER_LIST,
                        FACILITIES);
        if (CollectionUtils.isEmpty(facilityDataMap)) {
            facilityDataMap = Collections.EMPTY_MAP;
        }
        Map<String, String> facilityMap = facilityDataMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
        return facilityMap;
    }

    private FacilityResponse fetchAndMapToFacilityResponse(String key, Map<String, Object> m) {
        if (!m.containsKey(key)) {
            return new FacilityResponse(key);
        }
        Map<String, String> dataMap = (Map<String, String>) m.get(key);
        FacilityResponse facilityResponse = JsonUtils.convertValue(dataMap, FacilityResponse.class);
        return facilityResponse;
    }

    @EduCache(cache = "properties")
    public List<FacilityResponse> mapSchoolFacilitiesToDataObject(List<String> facilitiesAsString) {
        Map<String, Object> map =
                propertyReader.getPropertiesAsMapByKey(
                        EXPLORE_COMPONENT, FACILITIES_NAMESPACE, SCHOOL_FACILITY_KEY);
        List<FacilityResponse> facilities = facilitiesAsString.stream()
                .filter(StringUtils::isNotBlank)
                .map(key -> fetchAndMapToFacilityResponse(key, map))
                .peek(x -> x.setLogoUrl(CommonUtil.getAbsoluteUrl(x.getLogoUrl(), FACILITIES)))
                .collect(Collectors.toList());
        return facilities;
    }
}
