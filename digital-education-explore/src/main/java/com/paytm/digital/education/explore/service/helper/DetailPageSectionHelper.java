package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER_APP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTION_ORDER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.NAMESPACE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.KEY;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;

import com.paytm.digital.education.admin.request.SectionOrderRequest;
import com.paytm.digital.education.database.entity.Properties;
import com.paytm.digital.education.database.repository.PropertyRepository;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class DetailPageSectionHelper {

    private PropertyReader        propertyReader;
    private CommonMongoRepository commonMongoRepository;
    private PropertyRepository propertyRepository;

    public List<String> getSectionOrder(String entity, Client client) {
        String detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER;
        if (Client.APP.equals(client)) {
            detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER_APP;
        }
        Map<String, Object> sectionOrderMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, SECTION_ORDER_NAMESPACE,
                        detailPageSectionOrder);
        if (!CollectionUtils.isEmpty(sectionOrderMap)) {
            if (Objects.nonNull(sectionOrderMap.get(entity))) {
                return (List<String>) sectionOrderMap.get(entity);
            }
        }
        return null;
    }

    public List<String> updatePropertyMap(SectionOrderRequest sectionOrderRequest) {
        Client client = sectionOrderRequest.getClient();
        String detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER;
        if (Client.APP.equals(client)) {
            detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER_APP;
        }

        Map<String, Object> query = new HashMap<>();
        query.put(NAMESPACE, SECTION_ORDER_NAMESPACE);
        query.put(COMPONENT, EXPLORE_COMPONENT);
        query.put(KEY, detailPageSectionOrder);
        List<String> fields = Arrays.asList(ATTRIBUTES);
        Update update = new Update();
        List<String> sections = sectionOrderRequest.getSectionOrder();
        String entity = sectionOrderRequest.getEntity();
        String updateKey = ATTRIBUTES + "." + entity;
        update.set(updateKey, sections);
        commonMongoRepository.updateFirst(query, fields, update,
                java.util.Properties.class);

        Properties properties = propertyRepository
                .findByComponentAndNamespaceAndKey(EXPLORE_COMPONENT, SECTION_ORDER_NAMESPACE,
                        detailPageSectionOrder);

        if (properties != null) {
            Map<String, Object> sectionOrderMap =  properties.getAttributes();
            if (!CollectionUtils.isEmpty(sectionOrderMap) && Objects
                    .nonNull(sectionOrderMap.get(entity))) {
                return (List<String>) sectionOrderMap.get(entity);
            }
        }

        return Collections.emptyList();
    }
}
