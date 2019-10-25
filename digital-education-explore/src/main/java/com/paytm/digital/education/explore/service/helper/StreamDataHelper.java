package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_INSTITUTE;
import static com.paytm.digital.education.constant.ExploreConstants.RANKING_STREAM_NAMESPACE;

@Service
@AllArgsConstructor
public class StreamDataHelper {

    private PropertyReader propertyReader;

    public Map<String, String> getStreamMap() {
        Map<String, Object> streamDataMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, INSTITUTE_FILTER_NAMESPACE,
                        STREAM_INSTITUTE);

        if (CollectionUtils.isEmpty(streamDataMap)) {
            streamDataMap = Collections.EMPTY_MAP;
        }
        Map<String, String> streamMap = streamDataMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));

        return streamMap;
    }

    public Map<String, String> getStreamLabelMap() {
        Map<String, Object> streamDataMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, RANKING_STREAM_NAMESPACE,
                        STREAM_INSTITUTE);

        if (CollectionUtils.isEmpty(streamDataMap)) {
            streamDataMap = Collections.EMPTY_MAP;
        }
        Map<String, String> streamMap = streamDataMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));

        return streamMap;
    }
}
