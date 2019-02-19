package com.paytm.digital.education.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HierarchyIdentifierUtilsTests {

    public class localTest {

        private int           i;

        @JsonProperty("prop1")
        private float         j;

        @JsonProperty("nested_class")
        List<localNestedTest> lnTest;
    }

    private class localNestedTest {
        private int k;
    }

    @Test
    public void heirarchyTestPositive() {
        Map<String, String> expectedResult = new HashMap<>();
        String rootLevel = "";
        String nestedLevel = "nested_class";
        expectedResult.put("i", rootLevel);
        expectedResult.put("prop1", rootLevel);
        expectedResult.put("k", nestedLevel);
        expectedResult.put("this$0", rootLevel);
        expectedResult.put(nestedLevel, rootLevel);

        Map<String, String> result = HierarchyIdentifierUtils.getClassHierarchy(localTest.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void heirarchyTestNegative() {
        Map<String, String> expectedResult = new HashMap<>();
        String rootLevel = "";
        String nestedLevel = "nested_class";
        expectedResult.put("i", rootLevel);
        expectedResult.put("prop1", rootLevel);
        expectedResult.put(nestedLevel, rootLevel);

        Map<String, String> result = HierarchyIdentifierUtils.getClassHierarchy(localTest.class);
        Assert.assertNotEquals(expectedResult, result);
    }

}
