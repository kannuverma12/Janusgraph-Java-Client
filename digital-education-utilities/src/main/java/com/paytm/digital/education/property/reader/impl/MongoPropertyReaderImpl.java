package com.paytm.digital.education.property.reader.impl;

import com.paytm.digital.education.database.entity.Properties;
import com.paytm.digital.education.database.repository.PropertyRepository;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MongoPropertyReaderImpl implements PropertyReader {

    private PropertyRepository propertyRepository;

    @Override
    @Cacheable(value = "properties", unless = "#result == null ")
    public Map<String, Map<String, Object>> getPropertiesAsMap(List<String> keys, String component,
            String namespace) {
        List<Properties> propertiesList = propertyRepository
                .findByComponentAndAndNamespaceAndKeysIn(component, namespace, keys);
        if (!CollectionUtils.isEmpty(propertiesList)) {
            Map<String, Map<String, Object>> propertyMap = new HashMap<>();
            for (Properties properties : propertiesList) {
                propertyMap.put(properties.getKey(), properties.getAttributes());
            }
            return propertyMap;
        }
        return null;
    }

    @Override
    @Cacheable(value = "properties")
    public Map<String, Map<String, Object>> getPropertiesAsMap(String component, String namespace) {
        List<Properties> propertiesList = propertyRepository
                .findByComponentAndNamespace(component, namespace);
        if (!CollectionUtils.isEmpty(propertiesList)) {
            Map<String, Map<String, Object>> propertyMap = new HashMap<>();
            for (Properties properties : propertiesList) {
                propertyMap.put(properties.getKey(), properties.getAttributes());
            }
            return propertyMap;
        }
        return null;
    }

    @Override
    @Cacheable(value = "properties", unless = "#result == null", condition = "#key != "
            + "\"campus_engagement\" and #key != \"coaching_data_ingest\" and #key != "
            + "\"incremental\"")
    public Map<String, Object> getPropertiesAsMapByKey(String component, String namespace,
            String key) {
        Properties properties = propertyRepository
                .findByComponentAndNamespaceAndKey(component, namespace, key);
        if (properties != null) {
            return properties.getAttributes();
        }
        return null;
    }
}
