package com.paytm.digital.education.utility.service;

import com.paytm.digital.education.service.PDFService;
import com.paytm.digital.education.serviceimpl.PDFServiceImpl;
import com.paytm.digital.education.utility.testmodel.Person;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class PDFServiceTest {

    private PDFService pdfService = new PDFServiceImpl();

    @Test
    public void generatePDFFromTemplateTest() throws IOException {

        byte[] output = pdfService.generatePDFFromTemplate(
                "sample.xhtml",
                new Person("Girish &", 102, 6.6f, 54431, 9876543210L)
        );

        /* Uncomment and use this for generating expected pdf whenever UT changes data */
        // FileUtils.writeByteArrayToFile(new File("output.pdf"), output);   // generate in source root

        File expectedFile = new File(getClass().getResource("/expected_data.pdf").getFile());
        byte[] expectedData = FileUtils.readFileToByteArray(expectedFile);

        // removing pdf CreationDate, will be different for every pdf generation
        fillZero(output, 106, 127);
        fillZero(expectedData, 106, 127);

        // removing pdf ID sd
        fillZero(output, 1071, 1138);
        fillZero(expectedData, 1071, 1138);

        Assert.assertNotNull(output);
        Assert.assertArrayEquals(output, expectedData);
    }

    private void fillZero(byte[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            array[i] = 0;
        }
    }

}
