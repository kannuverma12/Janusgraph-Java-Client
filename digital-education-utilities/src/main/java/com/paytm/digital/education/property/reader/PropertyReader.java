package com.paytm.digital.education.property.reader;

import com.paytm.digital.education.database.entity.Properties;
import java.util.List;
import java.util.Map;

public interface PropertyReader {

    public Map<String, Map<String, Object>> getPropertiesAsMap(List<String> keys, String component,
            String namespace);

    public Map<String, Map<String, Object>> getPropertiesAsMap(String component,
            String namespace);

    public Map<String, Object> getPropertiesAsMapByKey(String component, String namespace,
            String key);
}
