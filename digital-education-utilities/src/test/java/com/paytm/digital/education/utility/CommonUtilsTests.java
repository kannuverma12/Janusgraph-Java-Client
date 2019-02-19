package com.paytm.digital.education.utility;

import org.junit.Assert;
import org.junit.Test;

public class CommonUtilsTests {

    @Test
    public void mainStringNull() {
        String originalStr = null;
        String key = null;
        String nextSeparator = null;
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertNull(result);
    }

    @Test
    public void keyNull() {
        String originalStr = "abc";
        String key = null;
        String nextSeparator = null;
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertNull(result);
    }


    @Test
    public void keyNotFound() {
        String originalStr = "abc";
        String key = "d";
        String nextSeparator = " ";
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertNull(result);
    }

    @Test
    public void separatorNull() {
        String originalStr = "abc";
        String key = "b";
        String nextSeparator = null;
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertNull(result);
    }

    @Test
    public void separatorNonNull() {
        String originalStr = "abc";
        String key = "b";
        String nextSeparator = "";
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertNull(result);
    }



    @Test
    public void separatorTest() {
        String originalStr =
                "@com.fasterxml.jackson.annotation.JsonProperty(index=-1, access=AUTO,value=exams_accepted, required=false, defaultValue=)";
        String key = "value=";
        String nextSeparator = ",";
        String expectedOutput = "exams_accepted";
        String result = CommonUtils.extractValueOfSubstringKey(originalStr, key, nextSeparator);
        Assert.assertEquals(expectedOutput, result);
    }

}
