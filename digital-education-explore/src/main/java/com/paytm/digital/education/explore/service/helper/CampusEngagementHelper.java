package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_ENGAGEMENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.GOOGLE_SHEETS_INFO;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.KEY;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;

@Service
@AllArgsConstructor
public class CampusEngagementHelper {
    private CommonMongoRepository commonMongoRepository;
    private PropertyReader        propertyReader;

    public void updatePropertyMap(String key, Object value) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(NAMESPACE, GOOGLE_SHEETS_INFO);
        queryObject.put(KEY, CAMPUS_ENGAGEMENT);
        List<String> fields = Arrays.asList(ATTRIBUTES);
        Update update = new Update();
        update.set(key, value);
        commonMongoRepository.updateFirst(queryObject, fields, update,
                Properties.class);
    }

    public Map<String, Object> getCampusEngagementProperties() {
        return propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, GOOGLE_SHEETS_INFO, CAMPUS_ENGAGEMENT);
    }
}
