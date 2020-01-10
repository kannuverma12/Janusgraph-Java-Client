package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DATE_TAB;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_GLOBAL_PRIORITY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_NAME_SYNONYMS;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_NAME_SYNONYMS_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_NGRAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_NGRAM_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SEARCH_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.RESULT;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_ANALYZER_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_INDEX_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_STREAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_STREAM_SUFFIX;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.SYLLABUS_TAB;
import static com.paytm.digital.education.constant.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;
import static com.paytm.digital.education.enums.es.FilterQueryType.TERMS;

import com.paytm.digital.education.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.ExamData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamLogoHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;



@Service
@RequiredArgsConstructor
public class ExamSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, Float>           searchFieldKeys;
    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static Set<String>                  sortFields;
    private final  SearchAggregateHelper        searchAggregateHelper;
    private final  ExamLogoHelper               examLogoHelper;
    private final  ExamSearchDatesHelper        examSearchDatesHelper;

    private static final Logger log =
            LoggerFactory.getLogger(ExamSearchServiceImpl.class);

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(LINGUISTIC_MEDIUM, TERMS);
        filterQueryTypeMap.put(SEARCH_EXAM_LEVEL, TERMS);
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(EXAM_FULL_NAME, EXAM_FULL_NAME_BOOST);
        searchFieldKeys.put(EXAM_SHORT_NAME, EXAM_SHORT_NAME_BOOST);
        searchFieldKeys.put(EXAM_NAME_SYNONYMS, EXAM_NAME_SYNONYMS_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME, EXAM_OFFICIAL_NAME_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME_NGRAM, EXAM_OFFICIAL_NAME_NGRAM_BOOST);

        sortFields = new HashSet<>();
    }

    @Override
    @Cacheable(value = "exam_search", key = "#searchRequest.cacheKey")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, ExamSearch.class);
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse,elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                EXAM_FILTER_NAMESPACE, EXAM_SEARCH_NAMESPACE, null, searchRequest.getClient());
        return searchResponse;
    }


    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_EXAM, SEARCH_INDEX_EXAM);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, ExamSearch.class);
        populateFilterFields(searchRequest, elasticRequest, ExamSearch.class, filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.getExamAggregateData(), ExamSearch.class);
        validateSortFields(searchRequest, sortFields);
        setSortOrderByStreamsPosition(searchRequest);
        populateSortFields(searchRequest, elasticRequest, ExamSearch.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest,  Client client) {
        List<ExamSearch> examSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        Map<Long, SearchBaseData> examDataMap = new HashMap<Long, SearchBaseData>();
        if (!CollectionUtils.isEmpty(examSearches)) {
            searchResults.setEntity(EducationEntity.EXAM);
            List<SearchBaseData> examDataList = new ArrayList<SearchBaseData>();
            examSearches.forEach(examSearch -> {
                ExamData examData = new ExamData();
                examData.setExamId(examSearch.getExamId());
                examData.setOfficialName(examSearch.getOfficialName());
                examData.setUrlDisplayName(
                        CommonUtil.convertNameToUrlDisplayName(examSearch.getOfficialName()));
                examData.setLogoUrl(examLogoHelper
                        .getExamLogoUrl((long) examSearch.getExamId(), examSearch.getImageLink()));
                List<String> dataAvailable = new ArrayList<>();
                if (!CollectionUtils.isEmpty(examSearch.getDataAvailable())) {
                    dataAvailable.addAll(examSearch.getDataAvailable());
                }
                if (!CollectionUtils.isEmpty(examSearch.getExamInstances())) {
                    examSearchDatesHelper.setAllImportantDates(examData);
                    dataAvailable.add(DATE_TAB);
                }
                if (Objects.nonNull(examSearch.getPaytmKeys())) {
                    ExamPaytmKeys examPaytmKeys = examSearch.getPaytmKeys();
                    examData.setCollegePredictorPid(examPaytmKeys.getCollegePredictorId());
                    examData.setFormId(examPaytmKeys.getFormId());
                }
                examData.setCtaList(ctaHelper.buildCTA(examData, client));
                examData.setDataAvailable(dataAvailable);
                examDataMap.put((long) examSearch.getExamId(), examData);
                examDataList.add(examData);
            });
            searchResults.setValues(examDataList);
        }
        searchResponse.setResults(searchResults);
        searchResponse.setEntityDataMap(examDataMap);
    }

    private void setSortOrderByStreamsPosition(SearchRequest searchRequest) {
        if (searchRequest.getFilter().containsKey(STREAM_IDS) && !CollectionUtils
                .isEmpty(searchRequest.getFilter().get(STREAM_IDS))) {
            if (CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
                searchRequest.setSortOrder(new LinkedHashMap<>());
            }
            List<Object> streamIds = searchRequest.getFilter().get(STREAM_IDS);
            for (Object streamId : streamIds) {
                searchRequest.getSortOrder()
                        .put(SEARCH_STREAM_PREFIX + streamId.toString() + SEARCH_STREAM_SUFFIX, ASC);
            }
            searchRequest.getSortOrder().put(EXAM_GLOBAL_PRIORITY, ASC);
        }
    }

}
