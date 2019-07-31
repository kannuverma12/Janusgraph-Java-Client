package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER_APP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTION_ORDER_NAMESPACE;

import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DetailPageSectionHelper {

    private PropertyReader propertyReader;

    public List<String> getSectionOrder(String entity, Client client) {
        String detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER;
        if (Client.APP.equals(client)) {
            detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER_APP;
        }
        Map<String, Object> sectionOrderMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, SECTION_ORDER_NAMESPACE,
                        detailPageSectionOrder);
        if (!CollectionUtils.isEmpty(sectionOrderMap)) {
            return (List<String>) sectionOrderMap.get(entity);
        }
        return null;
    }
}
