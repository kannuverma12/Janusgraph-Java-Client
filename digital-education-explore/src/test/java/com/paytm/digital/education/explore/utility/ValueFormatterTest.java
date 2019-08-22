package com.paytm.digital.education.explore.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class ValueFormatterTest {

    private static Map<String, Map<String, Object>> propertyMap = new HashMap<>();

    @BeforeClass
    public static void init() {
        Map<String, Object> attributes = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("affiliated");
        attributes.put("ignore", values);
        propertyMap.put("approvals", attributes);
    }


    @Test
    public void formatterIgnoreAll() {
        String keyName = "approvals";
        List<String> values = new ArrayList<>();
        values.add("Affiliated");
        values.add("Autonomous");
        List<String> expected = new ArrayList<>();
        expected.add("Autonomous");
        assertEquals(expected, CommonUtil.formatValues(propertyMap, keyName, values));
    }

    @Test
    public void formatterIgnoreOne() {
        String keyName = "approvals";
        List<String> values = new ArrayList<>();
        values.add("Autonomous");
        List<String> expected = new ArrayList<>();
        expected.add("Autonomous");
        assertEquals(expected, CommonUtil.formatValues(propertyMap, keyName, values));
    }

    @Test
    public void formatterNull() {
        String keyName = "approvals";
        List<String> values = null;
        assertNull(CommonUtil.formatValues(propertyMap, keyName, values));
    }

}
