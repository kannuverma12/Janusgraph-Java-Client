package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.HIGHLIGHTS_BASE_URL;
import static com.paytm.digital.education.constant.ExploreConstants.HIGHLIGHTS_TEMPLATE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.response.dto.detail.Attribute;
import com.paytm.digital.education.explore.template.TemplateProcessor;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DerivedAttributesHelper {

    private CommonMongoRepository commonMongoRepository;
    private TemplateProcessor     templateProcessor;

    public Map<String, List<Attribute>> getDerivedAttributes(Map<String, Object> highlightInputData,
            String entityType, Client client) {
        highlightInputData.put(HIGHLIGHTS_BASE_URL, CommonUtil.getHighLightBaseUrl(client));
        String highlightsTemplate = commonMongoRepository
                .getTemplate(HIGHLIGHTS_TEMPLATE, entityType);
        String highlights = templateProcessor
                .processTemplate(highlightsTemplate, HIGHLIGHTS_TEMPLATE, highlightInputData);
        if (StringUtils.isNotBlank(highlights)) {
            //return JsonUtils.fromJson(highlights, Map.class);
            Map<String, List<Attribute>> derivedMap = JsonUtils.fromJson(highlights,
                    new TypeReference<LinkedHashMap<String, ArrayList<Attribute>>>() {
                    });
            List<Attribute> highlightsResponseData = derivedMap.get(HIGHLIGHTS_TEMPLATE);
            if (!CollectionUtils.isEmpty(highlightsResponseData)) {
                int size = highlightsResponseData.size();
                if (Objects.isNull(highlightsResponseData.get(size - 1)) || Objects
                        .isNull(highlightsResponseData.get(size - 1).getTitle())) {
                    highlightsResponseData.remove(size - 1);
                }
            }
            derivedMap.put(HIGHLIGHTS_TEMPLATE, highlightsResponseData);
            return derivedMap;
        }
        return null;
    }


}
