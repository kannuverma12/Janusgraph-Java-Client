package com.paytm.digital.education.utility;

import org.junit.Assert;
import org.junit.Test;

public class OpenhtmltopdfServiceTest {

    @Test
    public void generatePDFTest() {

        String htmlFile = "rendered_output.xhtml";
        byte [] output = OpenHtmlToPdfUtility.htmlToPdf(
                FileUtility.getResourceFileAsString(htmlFile),
                FileUtility.getResourcePath(htmlFile)
                );
        Assert.assertNotNull(output);
        Assert.assertNotEquals(output.length, 0);
    }

}
