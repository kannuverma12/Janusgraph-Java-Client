package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.ConfigProperties;
import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.ExamSectionData;
import com.paytm.digital.education.explore.response.dto.search.ExamSubItemData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.constant.ExploreConstants.BROWSE_BY_EXAM_LEVEL;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_GLOBAL_PRIORITY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_LEVEL;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SEARCH_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_ANALYZER_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_INDEX_EXAM;

@Service
@AllArgsConstructor
public class ExamLandingPageServiceImpl extends AbstractSearchServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(ExamLandingPageServiceImpl.class);

    private static Map<String, Float>           searchFieldKeys;
    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static Set<String>                  sortFields;
    private        SearchAggregateHelper        searchAggregateHelper;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
        searchFieldKeys = new HashMap<>();

        sortFields = new HashSet<>();
        sortFields.add(EXAM_GLOBAL_PRIORITY);
    }

    @Override
    @Cacheable(value = "exam_landing_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, ExamSearch.class);
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        if (searchRequest.isFetchSearchResultsPerFilter()) {
            populateSearchResultsPerLevelAndStream(searchResponse, elasticResponse);
        }
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                EXAM_FILTER_NAMESPACE, EXAM_SEARCH_NAMESPACE, null,
                searchRequest.getClient());
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_EXAM, SEARCH_INDEX_EXAM);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, ExamSearch.class);
        populateFilterFields(searchRequest, elasticRequest, ExamSearch.class, filterQueryTypeMap);

        AggregateField[] aggregateFields =
                searchAggregateHelper.getTopHitsAggregateData(searchRequest.getDataPerFilter());
        populateAggregateFields(searchRequest, elasticRequest, aggregateFields, ExamSearch.class);
        validateSortFields(searchRequest, sortFields);
        populateSortFields(searchRequest, elasticRequest, ExamSearch.class);
        return elasticRequest;
    }

    private void populateSearchResultsPerLevelAndStream(SearchResponse searchResponse,
            ElasticResponse elasticResponse) {
        SearchResult searchResults = new SearchResult();
        searchResults.setEntity(EducationEntity.EXAM);
        Map<String, List<ExamSubItemData>> levelAndExamsMap = new HashMap<>();
        if (elasticResponse.getAggregationResponse().containsKey(EXAM_LEVEL)) {

            TopHitsAggregationResponse<ExamSearch> topHitsAggregationResponse =
                    (TopHitsAggregationResponse<ExamSearch>) elasticResponse
                            .getAggregationResponse().get(EXAM_LEVEL);

            if (Objects.nonNull(topHitsAggregationResponse.getDocumentsPerEntity())) {
                topHitsAggregationResponse.getDocumentsPerEntity()
                        .forEach((levelAndStreamIdKey, documents) -> {
                            String[] levelAndStreamArray =
                                    StringUtils.split(String.valueOf(levelAndStreamIdKey),
                                            ESConstants.KEY_SEPERATOR);
                            if (ArrayUtils.isNotEmpty(levelAndStreamArray)) {
                                String examLevel = levelAndStreamArray[0];
                                List<ExamSubItemData> examDataList =
                                        levelAndExamsMap.containsKey(examLevel)
                                                ?
                                                levelAndExamsMap.get(examLevel) :
                                                new ArrayList<>();

                                documents.forEach(examSearch -> {
                                    ExamSubItemData examData = getExamDetails(examSearch);
                                    examDataList.add(examData);
                                });
                                levelAndExamsMap.put(examLevel, examDataList);
                            }
                        });
            }
            ExamSectionData examsTopHitsData = ExamSectionData.builder()
                    .examsPerLevel(levelAndExamsMap)
                    .build();
            searchResults.setValues(Collections.singletonList(examsTopHitsData));
        }
        searchResponse.setResults(searchResults);
    }

    private ExamSubItemData getExamDetails(ExamSearch examSearch) {
        String logo = StringUtils.isNotEmpty(examSearch.getImageLink())
                ? CommonUtil.getAbsoluteUrl(examSearch.getImageLink(),
                BROWSE_BY_EXAM_LEVEL) : ConfigProperties.getExamPlaceholderLogoURL();
        return ExamSubItemData.builder()
                .examId(examSearch.getExamId())
                .officialName(examSearch.getOfficialName())
                .examShortName(examSearch.getExamShortName())
                .urlDisplayName(CommonUtil.convertNameToUrlDisplayName(
                        examSearch.getOfficialName()))
                .logoUrl(logo)
                .build();

    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest, Client client) {
    }

}
