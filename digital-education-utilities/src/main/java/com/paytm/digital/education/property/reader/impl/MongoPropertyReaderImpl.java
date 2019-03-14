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
    public Properties getProperty(String key, String component, String namespace) {
        return null;
    }

    @Override
    public List<Properties> getProperties(List<String> keys, String component, String namespace) {
        return null;
    }

    @Override
    @Cacheable(value = "properties")
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
}
