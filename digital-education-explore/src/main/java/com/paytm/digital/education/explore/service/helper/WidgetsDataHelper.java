package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.DATA_STRING;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.WIDGETS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WidgetsDataHelper {

    private PropertyReader propertyReader;

    public List<Widget> getWidgets(String entity, long excludeEntity) {
        Map<String, Object> widgetsDataMap =
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, WIDGETS);
        if (!CollectionUtils.isEmpty(widgetsDataMap)) {
            List<Widget> widgetList =
                    JsonUtils.convertValue(widgetsDataMap.get(DATA_STRING),
                            new TypeReference<List<Widget>>() {
                            });
            for (Widget widget : widgetList) {
                if (!CollectionUtils.isEmpty(widget.getData())) {
                    List<WidgetData> widgetDataList = new ArrayList<>();
                    for (WidgetData widgetData : widget.getData()) {
                        if (widgetData.getEntityId() != excludeEntity) {
                            widgetData.setUrlDisplayKey(CommonUtil
                                    .convertNameToUrlDisplayName(widgetData.getOfficialName()));
                            widgetDataList.add(widgetData);
                        }
                    }
                    widget.setData(widgetDataList);
                }
            }
            return widgetList;
        }
        return null;
    }

    public List<Widget> getWidgets(String entity, long excludeEntity, String domain) {
        Map<String, Object> widgetsDataMap =
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, WIDGETS);
        List<Widget> widgetList =
                JsonUtils.convertValue(widgetsDataMap.get(DATA_STRING),
                        new TypeReference<List<Widget>>() {
                        });
        if (!CollectionUtils.isEmpty(widgetsDataMap)) {
            for (Widget widget : widgetList) {
                List<WidgetData> widgetDataList =
                        widget.getData().stream()
                                .filter(widgetData -> (widgetData.getEntityId() != excludeEntity
                                        && widgetData.getStream().equals(domain)))
                                .collect(Collectors.toList());
                widget.setData(widgetDataList);
            }
        }
        return widgetList;
    }

    public List<Widget> getWidgets(String entity, long excludeEntity, long streamId) {
        Map<String, Object> widgetsDataMap =
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, WIDGETS);
        if (!CollectionUtils.isEmpty(widgetsDataMap)) {
            List<Widget> widgetList =
                    JsonUtils.convertValue(widgetsDataMap.get(DATA_STRING),
                            new TypeReference<List<Widget>>() {
                            });
            for (Widget widget : widgetList) {
                List<WidgetData> widgetDataList =
                        widget.getData().stream()
                                .filter(widgetData -> (widgetData.getEntityId() != excludeEntity
                                        && widgetData.getStreamId().equals(streamId)))
                                .collect(Collectors.toList());
                widget.setData(widgetDataList);
            }
            return widgetList;
        }
        return null;
    }
}
