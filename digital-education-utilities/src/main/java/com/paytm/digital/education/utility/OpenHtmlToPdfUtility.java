package com.paytm.digital.education.utility;


import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class OpenHtmlToPdfUtility {

    /**
     * This class contains openhtmltopdf library integration
     * Read for learning:
     * https://github.com/danfickle/openhtmltopdf/blob/open-dev-v1/docs/integration-guide.md
     * https://openhtmltopdf.com/template-guide.pdf
     *
     */

    private static final Logger logger = LoggerFactory.getLogger(OpenHtmlToPdfUtility.class);

    public static byte[] htmlToPdf(String html, URI baseUri) {

        ByteArrayOutputStream out = null;
        byte[] outByteArray = null;

        try {
            out = new ByteArrayOutputStream();

            String uri = baseUri.toString();
            logger.info("ACTUAL_BASE_URI:" + uri);
            uri = extractJarURL(uri);
            logger.info("CORRECTED_BASE_URI:" + uri);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, uri);
            builder.toStream(out);
            builder.run();

            outByteArray = out.toByteArray();
        } catch (Exception e) {
            logger.error("Exception occurred pdf generation for html:\n" + html
                    + e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error("Exception while closing output stream", e);
                }
            }
        }
        return outByteArray;
    }

    private static String extractJarURL(String url) {
        if (url.contains("jar!")) {
            /**
             *  media in jar file must be in format:
             *  jar:file:/<path_to_jar>!/<path_in_jar>/
             *  e.g:
             *  jar:file:/var/www/service/service.jar!/BOOT-INF/classes/
             */
            url = url.substring(url.lastIndexOf("file:"));
            url = url.replace("file:", "jar:file:");
            url = url.substring(0, url.lastIndexOf('/') + 1);
            url = url.replaceFirst("!/$", "/");
        }
        return url;
    }

}
