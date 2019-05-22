package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.HIGHLIGHTS_BASE_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.HIGHLIGHTS_TEMPLATE;

import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.Attribute;
import com.paytm.digital.education.explore.template.TemplateProcessor;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DerivedAttributesHelper {

    private CommonMongoRepository commonMongoRepository;
    private TemplateProcessor     templateProcessor;

    public Map<String, List<Attribute>> getDerivedAttributes(Map<String, Object> highlightData,
            String entityType) {
        highlightData.put(HIGHLIGHTS_BASE_URL, CommonUtil.getHighLightBaseUrl());
        String highlightsTemplate = commonMongoRepository
                .getTemplate(HIGHLIGHTS_TEMPLATE, entityType);
        String highlights = templateProcessor
                .processTemplate(highlightsTemplate, HIGHLIGHTS_TEMPLATE, highlightData);
        if (StringUtils.isNotBlank(highlights)) {
            return JsonUtils.fromJson(highlights, Map.class);
        }
        return null;
    }

}
