package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.enums.ClassifierDocType;
import com.paytm.digital.education.explore.enums.ClassifierSortType;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.es.model.ClassifierSearchDoc;
import com.paytm.digital.education.explore.es.model.ClassifierSortField;
import com.paytm.digital.education.explore.request.dto.search.Classification;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.explore.constants.ExploreConstants.CLASSIFIER_INDEX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CLASSIFIER_KEYWORD;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CLASSIFIER_KEYWORD_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CLASSIFIER_ANALYZER;

/**
 * This class classifies search query and apply appropriate filters and sort orders
 */
@Service
@AllArgsConstructor
public class ClassifierSearchService extends AbstractSearchServiceImpl {

    private Map<String, Float> searchFieldKeys;

    @PostConstruct
    private void init() {
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(CLASSIFIER_KEYWORD, CLASSIFIER_KEYWORD_BOOST);
    }

    public void classify(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        Classification classification = null;

        if (Objects.nonNull(searchRequest.getClassificationData())) {
            /*
             * Getting classification data from request when last call is classified. (Pagination).
             * */
            classification = searchRequest.getClassificationData();
        } else {
            /*
             * Generate classified data when classification object of FE request is null
             * */
            classification = generateClassification(searchRequest);
            searchRequest.setClassificationData(classification);
        }
        if (classification.isClassified()) {

            searchRequest.setTerm(classification.getTerm());
            /*
             * Priority will be given to FE filter per key.
             * */
            if (!CollectionUtils.isEmpty(classification.getFilters())) {
                if (CollectionUtils.isEmpty(searchRequest.getFilter())) {
                    searchRequest.setFilter(classification.getFilters());
                } else {
                    for (Map.Entry<String, List<Object>> filter : classification.getFilters()
                            .entrySet()) {
                        if (!searchRequest.getFilter().containsKey(filter.getKey())) {
                            searchRequest.getFilter().put(filter.getKey(), filter.getValue());
                        }
                    }
                }
            }
        }
    }

    private Classification generateClassification(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        List<ClassifierSearchDoc> classifiedDocuments = getClassification(searchRequest);
        Classification classificationData = new Classification();
        populateClassificationDataFromESDocuments(classifiedDocuments, classificationData,
                searchRequest.getTerm());
        return classificationData;
    }

    private void populateClassificationData(Classification classificationData,
            boolean curatedPlusLocationClassified, boolean isClassifiedOnStreamOnly,
            LinkedHashMap<String, DataSortOrder> otherSortParams,
            LinkedHashMap<String, DataSortOrder> streamSortParams,
            Map<String, List<Object>> filters, String mappedKeyword, String searchTerm,
            List<String> genericKeywords, boolean foundLocation) {
        /*
         * Using curation at document level for cases like (engineering colleges)
         * */
        boolean isGenericKeyword = false;
        if (!CollectionUtils.isEmpty(genericKeywords)) {
            isGenericKeyword = genericKeywords.contains(searchTerm.toLowerCase().trim());
        }
        /*
         * Search term priority for classified requests :
         * 1. Location plus curated get classified use FE search term (iit delhi)
         * 2. mapped (if found in classification)
         * 3. Using user query if it is classified on basis of stream only, and is not generic.
         * (not generic : deen dayan engineering college, generic : engineering colleges)
         * 4. Run match all (without term)
         *  */
        if (curatedPlusLocationClassified) {
            classificationData.setTerm(searchTerm);
        } else if (StringUtils.isNotBlank(mappedKeyword)) {
            classificationData.setTerm(mappedKeyword);
        } else if (isClassifiedOnStreamOnly && !isGenericKeyword) {
            classificationData.setTerm(searchTerm);
        } else {
            classificationData.setTerm(null);
        }
        if (!CollectionUtils.isEmpty(filters)) {
            classificationData.setFilters(filters);
        }
        classificationData.setLocationClassified(foundLocation);
        /*
         * Sort priority
         * 1. Sort params coming from FE request.
         * 2. ignore if curated plus location classification. (iit delhi)
         * 3. Relevance if user query is classified on basis of stream only, and is not generic.
         *  (not generic : deen dayan engineering college, generic : engineering colleges)
         * 4. Sort params present in classification
         * */
        if (!curatedPlusLocationClassified && (!isClassifiedOnStreamOnly || isGenericKeyword)) {
            LinkedHashMap<String, DataSortOrder> sortParams = new LinkedHashMap<>();
            sortParams.putAll(streamSortParams);
            sortParams.putAll(otherSortParams);
            if (!CollectionUtils.isEmpty(sortParams)) {
                classificationData.setSortParams(sortParams);
            }
        }
    }

    private void populateClassificationDataFromESDocuments(List<ClassifierSearchDoc> documents,
            Classification classificationData, String searchTerm) {
        Map<String, List<Object>> filters = new HashMap<>();
        List<String> genericKeywords = new ArrayList<>();
        //TODO : find a solution for curated keywords at ES level
        LinkedHashMap<String, DataSortOrder> streamSortParams = new LinkedHashMap<>();
        LinkedHashMap<String, DataSortOrder> otherSortParams = new LinkedHashMap<>();
        String mappedKeyword = "";
        boolean foundCurated = false;
        boolean foundLocation = false;
        boolean isClassifiedOnStreamOnly = true;

        if (!CollectionUtils.isEmpty(documents)) {
            classificationData.setClassified(true);
            for (ClassifierSearchDoc document : documents) {
                if (!CollectionUtils.isEmpty(document.getGenericKeywords())) {
                    genericKeywords.addAll(document.getGenericKeywords());
                }
                if (!CollectionUtils.isEmpty(document.getFilters())) {
                    CommonUtil.mergeTwoMapsContainigListAsValue(
                            (Map<String, List<Object>>) (Object) document.getFilters(), filters);
                }
                /*
                 * Stream wise sort params are in different map coz we are giving priority to stream sorting
                 * */
                if (!CollectionUtils.isEmpty(document.getSortOrder())) {
                    for (ClassifierSortField sortField : document.getSortOrder()) {
                        //TODO : Give preference to classified stream sort order
                        if (ClassifierSortType.STREAM.equals(sortField.getType())) {
                            streamSortParams.put(sortField.getField(), sortField.getOrder());
                        } else {
                            otherSortParams.put(sortField.getField(), sortField.getOrder());
                        }
                    }
                }
                /*
                 * Merging mapped keywords present in all classified docs.
                 * */
                if (StringUtils.isNotBlank(document.getMappedKeyword())) {
                    //mappedKeyword = mappedKeyword + " " + document.getMappedKeyword();
                    mappedKeyword = document.getMappedKeyword();
                }
                /*
                 * Flags to ignore sort when location is classified with a curated keyword
                 * */
                if (Objects.nonNull(document.getType())) {
                    if (!ClassifierDocType.STREAM.equals(document.getType())) {
                        isClassifiedOnStreamOnly = false;
                        if (ClassifierDocType.CURATED.equals(document.getType())) {
                            foundCurated = true;
                        } else if (ClassifierDocType.LOCATION.equals(document.getType())) {
                            foundLocation = true;
                        }
                    }
                }
            }
            boolean curatedPlusLocationClassified = foundCurated && foundLocation;
            populateClassificationData(classificationData, curatedPlusLocationClassified,
                    isClassifiedOnStreamOnly, otherSortParams, streamSortParams, filters,
                    mappedKeyword, searchTerm, genericKeywords, foundLocation);
        } else {
            classificationData.setClassified(false);
        }
    }

    private List<ClassifierSearchDoc> getClassification(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, ClassifierSearchDoc.class);
        if (elasticResponse.getTotalSearchResultsCount() != 0) {
            return (List<ClassifierSearchDoc>) elasticResponse.getDocuments();
        }
        return null;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setQueryTerm(searchRequest.getTerm());
        elasticRequest.setIndex(CLASSIFIER_INDEX);
        elasticRequest.setAnalyzer(CLASSIFIER_ANALYZER);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                ClassifierSearchDoc.class);
        elasticRequest.setSearchRequest(true);
        return elasticRequest;
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        return null;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest, Client client) {

    }
}
