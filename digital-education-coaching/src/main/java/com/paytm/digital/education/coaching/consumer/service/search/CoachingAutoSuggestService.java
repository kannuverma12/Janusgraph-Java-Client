package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.response.suggest.AutoSuggestData;
import com.paytm.digital.education.coaching.consumer.model.response.suggest.AutoSuggestResponse;
import com.paytm.digital.education.coaching.consumer.model.response.suggest.SuggestResult;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.search.model.AutoSuggestEsData;
import com.paytm.digital.education.search.service.CommonAutoSuggestionService;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.ENTITY_TYPE;

@Service
@AllArgsConstructor
public class CoachingAutoSuggestService {

    private static final Logger log = LoggerFactory.getLogger(CoachingAutoSuggestService.class);

    private CommonAutoSuggestionService commonAutoSuggestService;

    @Cacheable(value = "coaching_autosuggest")
    public AutoSuggestResponse getSuggestions(String searchTerm, List<EducationEntity> entities,
            int size) {

        ElasticResponse<AutoSuggestEsData> esResponse =
                commonAutoSuggestService.suggest(searchTerm, entities, size);
        return buildResponse(entities, esResponse);
    }

    private AutoSuggestResponse buildResponse(List<EducationEntity> entities,
            ElasticResponse<AutoSuggestEsData> esResponse) {
        if (entities.size() == 1) {
            return extractDataFromSearchResults(entities.get(0), esResponse);
        } else {
            return extractDataFromTopHitsResponse(esResponse);
        }
    }


    private AutoSuggestResponse extractDataFromSearchResults(EducationEntity entity,
            ElasticResponse<AutoSuggestEsData> esResponse) {

        List<AutoSuggestEsData> documents = esResponse.getDocuments();
        List<SuggestResult> responseDocList = convertEsDataToResponse(documents);

        AutoSuggestData dataPerEntity = new AutoSuggestData();
        dataPerEntity.setEntityType(entity.name().toLowerCase());
        dataPerEntity.setResults(responseDocList);

        List<AutoSuggestData> suggestData = new ArrayList<>();
        suggestData.add(dataPerEntity);

        AutoSuggestResponse autosuggestResponse = new AutoSuggestResponse();
        autosuggestResponse.setData(suggestData);

        return autosuggestResponse;
    }

    private List<SuggestResult> convertEsDataToResponse(List<AutoSuggestEsData> documents) {
        List<SuggestResult> responseResultDocList = new ArrayList<>();
        documents.forEach(esDocument -> {
            SuggestResult responseDoc = new SuggestResult(esDocument.getEntityId(),
                    esDocument.getOfficialName());
            responseDoc.setUrlDisplayKey(
                    CommonUtil.convertNameToUrlDisplayName(esDocument.getOfficialName()));

            if (StringUtils.isNotBlank(esDocument.getLogo())) {
                responseDoc.setLogo(CommonUtil
                        .getLogoLink(esDocument.getLogo(), EducationEntity.EXAM));
            }

            responseResultDocList.add(responseDoc);
        });
        return responseResultDocList;
    }

    private AutoSuggestResponse extractDataFromTopHitsResponse(
            ElasticResponse<AutoSuggestEsData> esResponse) {

        Map<String, AggregationResponse> aggregationResponse = esResponse.getAggregationResponse();

        if (!aggregationResponse.containsKey(ENTITY_TYPE)) {
            log.error("Aggregations not found for given entity type.");
        }

        TopHitsAggregationResponse<AutoSuggestEsData> topHitsPerEntity =
                (TopHitsAggregationResponse<AutoSuggestEsData>) aggregationResponse
                        .get(ENTITY_TYPE);

        List<AutoSuggestData> suggestData = new ArrayList<>();

        topHitsPerEntity.getDocumentsPerEntity().forEach((key, documents) -> {
            AutoSuggestData dataPerEntity = new AutoSuggestData();

            List<SuggestResult> responseResultDocList =
                    convertEsDataToResponse(documents);
            dataPerEntity.setEntityType(key.getKey());
            dataPerEntity.setResults(responseResultDocList);
            suggestData.add(dataPerEntity);
        });

        AutoSuggestResponse autosuggestResponse = new AutoSuggestResponse();
        autosuggestResponse.setData(suggestData);

        return autosuggestResponse;
    }



}
