package com.paytm.digital.education.utility;

import com.paytm.digital.education.config.ConfigProperties;
import com.paytm.digital.education.dto.OfficialAddress;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSES;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_STREAMS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES;
import static com.paytm.digital.education.constant.ExploreConstants.AFFILIATED;
import static com.paytm.digital.education.constant.ExploreConstants.AFFILIATED_TO;
import static com.paytm.digital.education.constant.ExploreConstants.APPROVED_BY;
import static com.paytm.digital.education.constant.ExploreConstants.APP_FOOTER;
import static com.paytm.digital.education.constant.ExploreConstants.AUTONOMOUS;
import static com.paytm.digital.education.constant.ExploreConstants.BANNER_MID;
import static com.paytm.digital.education.constant.ExploreConstants.CONSTITUENT;
import static com.paytm.digital.education.constant.ExploreConstants.CONSTITUENT_OF;
import static com.paytm.digital.education.constant.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.constant.ExploreConstants.IGNORE_VALUES;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_TYPE;
import static com.paytm.digital.education.constant.ExploreConstants.LOCATIONS;
import static com.paytm.digital.education.constant.ExploreConstants.RANKING_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.STANDALONE_INSTITUTE;
import static com.paytm.digital.education.constant.ExploreConstants.STREAMS;
import static com.paytm.digital.education.constant.ExploreConstants.TOP_EXAMS_APP;
import static com.paytm.digital.education.constant.ExploreConstants.UGC;

@UtilityClass
public class CommonUtil {

    public String getLogoLink(String logo, EducationEntity educationEntity) {
        String absoluteUrl = ConfigProperties.getBaseUrl();
        if (EducationEntity.EXAM.equals(educationEntity)) {
            absoluteUrl = absoluteUrl + ConfigProperties.getExamLogoPrefix() + logo;
        } else {
            absoluteUrl = absoluteUrl + ConfigProperties.getLogoImagePrefix() + logo;
        }
        return absoluteUrl;
    }

    public String getAbsoluteUrl(String relativeUrl, String type) {
        StringBuilder urlBuilder = new StringBuilder(ConfigProperties.getBaseUrl());
        switch (type) {
            case APP_FOOTER:
                urlBuilder.append(ConfigProperties.getAppFooterPrefix());
                break;
            case FACILITIES:
                urlBuilder.append(ConfigProperties.getFacilitiesIconPrefix());
                break;
            case STREAMS:
            case COACHING_STREAMS:
                urlBuilder.append(ConfigProperties.getStreamIconPrefix());
                break;
            case LOCATIONS:
                urlBuilder.append(ConfigProperties.getLocationIconPrefix());
                break;
            case BANNER_MID:
                urlBuilder.append(ConfigProperties.getBannerPrefix());
                break;
            case TOP_EXAMS_APP:
            case COACHING_TOP_EXAMS:
                urlBuilder.append(ConfigProperties.getLogoExamPrefix());
                break;
            case RANKING_LOGO:
                urlBuilder.append(ConfigProperties.getRankingLogo());
                break;
            case COACHING_COURSES:
                urlBuilder.append(ConfigProperties.getCoachingS3Path())
                        .append("/")
                        .append(ConfigProperties.getEnvProfile())
                        .append(ConfigProperties.getCoachingCourseLogoPrefix());
                break;
            case TOP_COACHING_INSTITUTES:
                urlBuilder.append(ConfigProperties.getCoachingS3Path())
                        .append("/")
                        .append(ConfigProperties.getEnvProfile())
                        .append(ConfigProperties.getCoachingInstituteLogoPrefix());
                break;
            default:
                urlBuilder.append(ConfigProperties.getLogoImagePrefix());
        }
        urlBuilder.append(relativeUrl);
        return urlBuilder.toString();
    }

    public String getHighLightBaseUrl(Client client) {
        if (Client.APP.equals(client)) {
            return ConfigProperties.getBaseUrl() + ConfigProperties.getHighlightsIconPrefixApp();
        }
        return ConfigProperties.getBaseUrl() + ConfigProperties.getHighlightsIconPrefix();
    }

    public OfficialAddress getOfficialAddress(String state, String city, String phone, String url,
            com.paytm.digital.education.database.entity.OfficialAddress officialAddress) {
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

    public String removeWordsFromString(String inputString, List<String> stopWords,
            String seperator) {
        if (CollectionUtils.isEmpty(stopWords)) {
            return inputString;
        }
        String outputString = "";
        String[] inputStringArray = inputString.split(seperator);
        for (String word : inputStringArray) {
            if (!stopWords.contains(word)) {
                outputString = outputString + seperator + word;
            }
        }
        return outputString.trim();
    }

    public void mergeTwoMapsContainigListAsValue(Map<String, List<Object>> inputMap,
            Map<String, List<Object>> outputMap) {
        for (Map.Entry<String, List<Object>> data : inputMap
                .entrySet()) {
            if (outputMap.containsKey(data.getKey())) {
                outputMap.get(data.getKey())
                        .addAll(data.getValue());
            } else {
                outputMap.put(data.getKey(), data.getValue());
            }
        }
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
            displayName = toCamelCase(keyName);
        }
        return displayName;
    }


    public void convertStringValuesToLowerCase(Map<String, List<Object>> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            for (Map.Entry<String, List<Object>> filter : filters.entrySet()) {
                if (!CollectionUtils.isEmpty(filter.getValue())
                        && filter.getValue().get(0) instanceof String) {
                    for (int i = 0; i < filter.getValue().size(); i++) {
                        filter.getValue().set(i, ((String) filter.getValue().get(i)).toLowerCase());
                    }
                }
            }
        }
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

    public String toCamelCase(final String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }

        final StringBuilder ret = new StringBuilder(key.length());

        for (final String word : key.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == key.length())) {
                ret.append(" ");
            }
        }

        return ret.toString();
    }

    public int getIndexForMaxValue(List<Integer> data) {
        int max = data.get(0);
        int maxIndex = 0;
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i) > max) {
                max = data.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public boolean areAllEqual(List<Integer> data) {
        if (!CollectionUtils.isEmpty(data)) {
            int val = data.get(0);
            for (int i = 1; i < data.size(); i++) {
                if (data.get(i) != val) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areAllDoubleEqual(List<Double> doubleValues) {
        if (!CollectionUtils.isEmpty(doubleValues)) {
            Double val = doubleValues.get(0);
            for (int i = 1; i < doubleValues.size(); i++) {
                if (Double.compare(val, doubleValues.get(i)) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public String convertNameToUrlDisplayName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]+", "-").toLowerCase();
    }
}
