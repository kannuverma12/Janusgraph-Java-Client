package com.paytm.digital.education.property.reader;

import com.paytm.digital.education.database.entity.Properties;

import java.util.List;
import java.util.Map;

public interface PropertyReader {

    public Properties getProperty(String key, String component, String namespace);

    public List<Properties> getProperties(List<String> keys, String component, String namespace);

    public Map<String, Map<String, Object>> getPropertiesAsMap(List<String> keys, String component,
            String namespace);
}
