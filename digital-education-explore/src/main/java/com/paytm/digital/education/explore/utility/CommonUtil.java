package com.paytm.digital.education.explore.utility;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AFFILIATED;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AFFILIATED_TO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVED_BY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTONOMOUS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BANNER_MID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CONSTITUENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CONSTITUENT_OF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IGNORE_VALUES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LOCATIONS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STANDALONE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.UGC;

import com.paytm.digital.education.explore.config.ConfigProperties;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class CommonUtil {

    public String getLogoLink(String logo) {
        return ConfigProperties.getBaseUrl() + ConfigProperties.getLogoImagePrefix() + logo;
    }

    public String getAbsoluteUrl(String relativeUrl, String type) {
        StringBuilder urlBuilder = new StringBuilder(ConfigProperties.getBaseUrl());
        switch (type) {
            case FACILITIES:
                urlBuilder.append(ConfigProperties.getFacilitiesIconPrefix());
                break;
            case STREAMS:
                urlBuilder.append(ConfigProperties.getStreamIconPrefix());
                break;
            case LOCATIONS:
                urlBuilder.append(ConfigProperties.getLocationIconPrefix());
                break;
            case BANNER_MID:
                urlBuilder.append(ConfigProperties.getBannerPrefix());
                break;
            default:
                urlBuilder.append(ConfigProperties.getLogoImagePrefix());
        }
        urlBuilder.append(relativeUrl);
        return urlBuilder.toString();
    }

    public String getHighLightBaseUrl() {
        return ConfigProperties.getBaseUrl() + ConfigProperties.getHighlightsIconPrefix();
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

    public Map<String, Object> readDataFromExcel(final String filePath) throws IOException,
            InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
        DataFormatter dataFormatter = new DataFormatter();
        Map<String, Object> map = new HashMap<>();
        workbook.forEach(sheet -> {
            System.out.println("=> " + sheet.getSheetName());
            boolean start = true;
            List<String> headers = new ArrayList<>();
            List<Object> dataList = new ArrayList<>();
            for (Row row : sheet) {
                Map<String, Object> data = new HashMap<>();
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if (start == true) {
                        headers.add(cellValue.replace(" ", "_").toLowerCase());
                    } else {
                        data.put(headers.get(cell.getColumnIndex()), cellValue);
                    }
                }
                if (start == true) {
                    start = false;
                } else {
                    dataList.add(data);
                }
            }
            map.put(sheet.getSheetName().toLowerCase(), dataList);
        });
        workbook.close();
        return map;
    }
}
