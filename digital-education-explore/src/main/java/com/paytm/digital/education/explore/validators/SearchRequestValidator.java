package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

@Slf4j
@AllArgsConstructor
@Component
public class SearchRequestValidator {

    private Validator validator;

    public void validate(SearchRequest searchRequest) {
        if (searchRequest == null) {
            throw new RuntimeException("Bad Request. Search Request body should be present.");
        }
        Set<ConstraintViolation<SearchRequest>> voilations = validator.validate(searchRequest);
        if (!voilations.isEmpty()) {
            StringBuilder messagesBuilder = new StringBuilder();
            voilations.forEach(voilation -> {
                messagesBuilder.append(voilation.getMessage()).append(",");
            });
            throw new RuntimeException("Bad Request. Error : " + StringUtils
                    .substringBeforeLast(messagesBuilder.toString(), ","));
        }
        
        if (!(StringUtils.isNotBlank(searchRequest.getFieldGroup()) ^ !CollectionUtils
                .isEmpty(searchRequest.getFields()))) {
            throw new RuntimeException("Either fields or field_group is accepted.");
        }
    }
}
