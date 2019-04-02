package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DATA_STRING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.WIDGETS;

import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WidgetsDataHelper {

    private PropertyReader propertyReader;

    public List<Widget> getWidgets(String entity) {
        Map<String, Object> widgetsDataMap = propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, WIDGETS);
        if (!CollectionUtils.isEmpty(widgetsDataMap)) {
            return (List<Widget>) widgetsDataMap.get(DATA_STRING);
        }
        return null;
    }
}
