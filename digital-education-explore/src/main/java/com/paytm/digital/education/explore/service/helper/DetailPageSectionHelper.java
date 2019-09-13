package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAIL_PAGE_SECTION_ORDER_APP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTION_ORDER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAILS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LANDING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTIONS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUCCESS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FAILED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.NAMESPACE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.KEY;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;

import com.paytm.digital.education.admin.request.SectionOrderRequest;
import com.paytm.digital.education.admin.response.SectionOrderResponse;
import com.paytm.digital.education.database.entity.Properties;
import com.paytm.digital.education.database.repository.PropertyRepository;
import com.paytm.digital.education.explore.database.entity.Page;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.database.repository.PageRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.Arrays;

@Slf4j
@Service
@AllArgsConstructor
public class DetailPageSectionHelper {

    private PropertyReader        propertyReader;
    private CommonMongoRepository commonMongoRepository;
    private PropertyRepository propertyRepository;
    private PageRepository pageRepository;

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

    public SectionOrderResponse getSectionOrder(String page, String entity, Client client) {
        SectionOrderResponse orderResponse = new SectionOrderResponse();
        if (page.equalsIgnoreCase(DETAILS)) {
            String detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER;
            if (Client.APP.equals(client)) {
                detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER_APP;
            }

            Map<String, Object> sectionMap = propertyReader
                    .getPropertiesAsMapByKey(EXPLORE_COMPONENT, SECTION_ORDER_NAMESPACE,
                            detailPageSectionOrder);
            if (!CollectionUtils.isEmpty(sectionMap)) {
                if (Objects.nonNull(sectionMap.get(entity))) {
                    orderResponse.setMessage("Section order found.");
                    orderResponse.setEntity(entity);
                    orderResponse.setPage(page);
                    orderResponse.setStatus(SUCCESS);
                    orderResponse.setSectionOrder((List<String>) sectionMap.get(entity));
                } else {
                    orderResponse.setStatus(FAILED);
                    orderResponse.setError("Sections order no found.");
                    orderResponse.setMessage("No Entity Found.");
                }
            } else {
                orderResponse.setStatus(FAILED);
                orderResponse.setError("Sections order no found.");
                orderResponse.setMessage("No Entity Found.");
            }
        } else if (page.equalsIgnoreCase(LANDING)) {
            Page pageEntity = pageRepository.getPageByName(entity);
            if (Objects.nonNull(pageEntity)) {
                List<String> pageSectionNames = pageEntity.getSections();
                if (Objects.nonNull(pageSectionNames)) {
                    orderResponse.setMessage("Section order found.");
                    orderResponse.setEntity(entity);
                    orderResponse.setPage(page);
                    orderResponse.setStatus(SUCCESS);
                    orderResponse.setSectionOrder(pageSectionNames);
                }
            } else {
                orderResponse.setStatus(FAILED);
                orderResponse.setError("Sections order no found.");
                orderResponse.setMessage("No Entity Found.");
                log.info("No Entity Found.");
            }
        } else {
            orderResponse.setStatus(FAILED);
            orderResponse.setError("Sections order no found.");
            orderResponse.setMessage("Invalid Page.");
        }
        if (CollectionUtils.isEmpty(orderResponse.getSectionOrder())) {
            orderResponse.setSectionOrder(Collections.emptyList());
        }

        return orderResponse;
    }

    public SectionOrderResponse updatePropertyMap(SectionOrderRequest sectionOrderRequest) {
        SectionOrderResponse orderResponse = new SectionOrderResponse();
        String page = sectionOrderRequest.getPage();
        if (Objects.nonNull(page)) {
            if (page.equalsIgnoreCase(DETAILS)) {
                orderResponse = updateDetailsOrderSection(sectionOrderRequest);
            } else if (page.equalsIgnoreCase(LANDING)) {
                orderResponse = updateLandingSectionOrder(sectionOrderRequest);
            } else {
                orderResponse.setStatus(FAILED);
                orderResponse.setMessage("Sections update failed");
                orderResponse.setError("Page not found");
            }
        } else {
            orderResponse.setStatus(FAILED);
            orderResponse.setMessage("Sections update failed");
            orderResponse.setError("Page not found");
        }
        if (CollectionUtils.isEmpty(orderResponse.getSectionOrder())) {
            orderResponse.setSectionOrder(Collections.emptyList());
        }
        return orderResponse;
    }

    private SectionOrderResponse updateLandingSectionOrder(SectionOrderRequest sectionOrderRequest) {
        SectionOrderResponse orderResponse = new SectionOrderResponse();
        Page pageEntity = pageRepository.getPageByName(sectionOrderRequest.getEntity());
        if (Objects.nonNull(pageEntity) && Objects.nonNull(pageEntity.getSections())) {
            List<String> validatedSectionOrder =
                    validateRequestSections(sectionOrderRequest.getSectionOrder(),
                            pageEntity.getSections());
            if (validatedSectionOrder.size() == pageEntity.getSections().size()) {
                Map<String, Object> query = new HashMap<>();
                query.put(NAME, sectionOrderRequest.getEntity());
                List<String> fields = Arrays.asList(SECTIONS);
                Update update = new Update();
                update.set(SECTIONS, validatedSectionOrder);
                commonMongoRepository.updateFirst(query, fields, update, Page.class);
            } else {
                orderResponse.setStatus(FAILED);
                orderResponse.setMessage("Sections update failed");
                orderResponse.setError("Invalid section order passed");
                return orderResponse;
            }
        } else {
            orderResponse.setStatus(FAILED);
            orderResponse.setMessage("Sections update failed");
            orderResponse.setError("Invalid Entity");
            return orderResponse;
        }

        Page updatedPage = pageRepository.getPageByName(sectionOrderRequest.getEntity());
        if (Objects.nonNull(updatedPage)) {
            if (!CollectionUtils.isEmpty(updatedPage.getSections())) {
                orderResponse.setStatus(SUCCESS);
                orderResponse.setMessage("Sections update successful");
                orderResponse.setEntity(sectionOrderRequest.getEntity());
                orderResponse.setSectionOrder(updatedPage.getSections());
            } else {
                orderResponse.setStatus(FAILED);
                orderResponse.setMessage("Sections update failed");
                orderResponse.setError("Section order does not exists");
            }
        }
        return orderResponse;
    }

    private List<String> validateRequestSections(List<String> requestSectionOrder,
            List<String> dbSectionOrder) {
        List<String> validatedList = new ArrayList<>();
        for (String section : requestSectionOrder) {
            if (Objects.nonNull(dbSectionOrder) && dbSectionOrder.contains(section)) {
                validatedList.add(section);
            }
        }
        return validatedList;
    }

    private SectionOrderResponse updateDetailsOrderSection(SectionOrderRequest sectionOrderRequest) {
        SectionOrderResponse orderResponse = new SectionOrderResponse();
        Client client = sectionOrderRequest.getClient();
        String detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER;
        if (Client.APP.equals(client)) {
            detailPageSectionOrder = DETAIL_PAGE_SECTION_ORDER_APP;
        }

        List<String> sections = sectionOrderRequest.getSectionOrder();
        List<String> dbSections = getDBSectionList(sectionOrderRequest);

        List<String> validatedSectionOrder =
                validateRequestSections(sectionOrderRequest.getSectionOrder(), dbSections);
        String entity = sectionOrderRequest.getEntity();
        if (Objects.nonNull(dbSections) && (validatedSectionOrder.size() == dbSections.size())) {
            Update update = new Update();
            String updateKey = ATTRIBUTES + "." + entity;
            update.set(updateKey, sections);
            Map<String, Object> query = new HashMap<>();
            query.put(COMPONENT, EXPLORE_COMPONENT);
            query.put(NAMESPACE, SECTION_ORDER_NAMESPACE);
            query.put(KEY, detailPageSectionOrder);
            List<String> fields = Arrays.asList(ATTRIBUTES);

            commonMongoRepository.updateFirst(query, fields, update,
                    java.util.Properties.class);
        } else {
            orderResponse.setStatus(FAILED);
            orderResponse.setMessage("Sections update failed");
            orderResponse.setError("Invalid section order passed");
            return orderResponse;
        }

        Properties properties = propertyRepository
                .findByComponentAndNamespaceAndKey(EXPLORE_COMPONENT, SECTION_ORDER_NAMESPACE,
                        detailPageSectionOrder);

        if (properties != null) {
            Map<String, Object> sectionOrderMap =  properties.getAttributes();
            if (!CollectionUtils.isEmpty(sectionOrderMap) && Objects
                    .nonNull(sectionOrderMap.get(entity))) {
                orderResponse.setStatus(SUCCESS);
                orderResponse.setMessage("Sections updated");
                orderResponse.setEntity(entity);
                orderResponse.setSectionOrder((List<String>) sectionOrderMap.get(entity));
            }
        }

        if (CollectionUtils.isEmpty(orderResponse.getSectionOrder())) {
            orderResponse.setSectionOrder(Collections.emptyList());
        }

        return orderResponse;
    }

    private List<String> getDBSectionList(SectionOrderRequest sectionOrderRequest) {
        SectionOrderResponse dbResponse = getSectionOrder(sectionOrderRequest.getPage(),
                sectionOrderRequest.getEntity(), sectionOrderRequest.getClient());
        List<String> dbSections = null;
        if (Objects.nonNull(dbResponse) && Objects.nonNull(dbResponse.getStatus())) {
            if (dbResponse.getStatus().equals(SUCCESS)) {
                dbSections = getSectionOrder(sectionOrderRequest.getPage(),
                        sectionOrderRequest.getEntity(), sectionOrderRequest.getClient())
                        .getSectionOrder();
            }
        }
        return dbSections;
    }
}
