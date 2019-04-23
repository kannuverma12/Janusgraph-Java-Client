package com.paytm.digital.education.explore.utility;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AFFILIATED;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AFFILIATED_TO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVED_BY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTONOMOUS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CONSTITUENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CONSTITUENT_OF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IGNORE_VALUES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STANDALONE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.UGC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.explore.config.ConfigProperties;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public String getLogoLink(String logo) {
        return ConfigProperties.getBaseUrl() + ConfigProperties.getLogoImagePrefix() + logo;
    }

    public OfficialAddress getOfficialAddress(String state, String city, String phone, String url,
            com.paytm.digital.education.explore.database.entity.OfficialAddress officialAddress) {
        OfficialAddress address = new OfficialAddress();
        address.setState(state);
        address.setCity(city);
        address.setPhone(phone);
        address.setUrl(url);
        if (officialAddress != null) {
            address.setLatLon(officialAddress.getLatLon());
            address.setPinCode(officialAddress.getPinCode());
            address.setPlaceId(officialAddress.getPlaceId());
            address.setStreetAddress(officialAddress.getStreetAddress());
        }
        return address;
    }

    public List<String> formatValues(Map<String, Map<String, Object>> propertyMap,
            String keyName,
            List<String> values) {
        if (!CollectionUtils.isEmpty(values) && !CollectionUtils.isEmpty(propertyMap)
                && propertyMap.containsKey(keyName)) {
            if (propertyMap.get(keyName).containsKey(IGNORE_VALUES)) {
                List<String> ignoreValues =
                        (List<String>) propertyMap.get(keyName).get(IGNORE_VALUES);
                List<String> displayValues = new ArrayList<>();
                for (String value : values) {
                    if (!ignoreValues.contains(value.toLowerCase())) {
                        displayValues.add(value);
                    }
                }
                if (!CollectionUtils.isEmpty(displayValues)) {
                    return displayValues;
                } else {
                    return null;
                }
            }
        }
        return values;
    }

    public String getDisplayName(Map<String, Object> propertyMap,
            String keyName) {
        String displayName;
        if (!CollectionUtils.isEmpty(propertyMap)
                && propertyMap.containsKey(keyName)) {
            displayName = propertyMap.get(keyName)
                    .toString();
        } else {
            displayName = keyName;
        }
        return displayName;
    }

    public String getDisplayName(Map<String, Map<String, Object>> propertyMap,
            String fieldName,
            String keyName) {
        String displayName;
        if (!CollectionUtils.isEmpty(propertyMap)
                && propertyMap.containsKey(fieldName) && propertyMap

                .get(fieldName).containsKey(keyName)) {
            displayName = propertyMap.get(fieldName).get(keyName)
                    .toString();
        } else {
            displayName = keyName;
        }
        return displayName;
    }


    public void convertStringValuesToLowerCase(Map<String, List<Object>> filters) {
        for (Map.Entry<String, List<Object>> filter : filters.entrySet()) {
            if (!CollectionUtils.isEmpty(filter.getValue())
                    && filter.getValue().get(0) instanceof String) {
                for (int i = 0; i < filter.getValue().size(); i++) {
                    filter.getValue().set(i, ((String) filter.getValue().get(i)).toLowerCase());
                }
            }
        }
    }
    
    public String encodeString(String url) {
        return url.replace(" ", "%20");
    }
    public Map<String, String> getApprovals(List<String> approvals, String universityName) {
        if (!CollectionUtils.isEmpty(approvals)) {
            Map<String, String> output = new HashMap<>();
            for (String value : approvals) {
                switch (value.toLowerCase()) {
                    case AFFILIATED:
                        if (StringUtils.isNotBlank(universityName)) {
                            output.put(AFFILIATED_TO, universityName);
                        }
                        break;
                    case UGC:
                        output.put(APPROVED_BY, UGC.toUpperCase());
                        break;
                    case CONSTITUENT:
                        if (StringUtils.isNotBlank(universityName)) {
                            output.put(CONSTITUENT_OF, universityName);
                        }
                        break;
                    case AUTONOMOUS:
                        //case STANDALONE_INSTITUTE:
                        output.put(INSTITUTE_TYPE, STANDALONE_INSTITUTE);
                        break;
                    //                    case STATE_LEGISLATURE:
                    //                        output.put(GOVERNED_BY, STATE_LEGISLATURE);
                    //                        break;
                    default:
                }
            }
            if (!CollectionUtils.isEmpty(output)) {
                return output;
            }
        }
        return null;
    }
}
