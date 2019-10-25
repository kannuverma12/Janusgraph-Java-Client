package com.paytm.digital.education.search.service;

import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.search.model.AutoSuggestEsData;

import java.util.List;

public interface CommonAutoSuggestionService {

    ElasticResponse<AutoSuggestEsData> suggest(String searchTerm, List<EducationEntity> entities,
            int size);
}
