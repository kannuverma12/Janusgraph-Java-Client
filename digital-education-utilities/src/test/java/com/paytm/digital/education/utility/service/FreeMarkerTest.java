package com.paytm.digital.education.utility.service;

import com.paytm.digital.education.service.TemplateService;
import com.paytm.digital.education.serviceimpl.FreeMarkerTemplateService;
import com.paytm.digital.education.utility.FileUtility;
import com.paytm.digital.education.utility.testmodel.Person;
import org.junit.Assert;
import org.junit.Test;

public class FreeMarkerTest {

    private TemplateService templateService = new FreeMarkerTemplateService();

    @Test
    public void templateTest() {

        String renderedOutput = templateService.renderTemplate(
                "sample.xhtml",
                new Person("Girish &", 102, 6.6f, 54431, 9876543210L)
        );

        String expectedOutput = FileUtility.getResourceFileAsString("rendered_output.xhtml");

        Assert.assertEquals(renderedOutput.trim(), expectedOutput);
    }

}
