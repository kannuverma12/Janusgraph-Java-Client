package com.paytm.digital.education.explore.template;

import static com.paytm.digital.education.constant.ExploreConstants.FTL_CURRENT_VERSION;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;


@Service
@AllArgsConstructor
public class TemplateProcessor {

    private static final Logger log = LoggerFactory.getLogger(TemplateProcessor.class);

    public String processTemplate(String formattedTemplate, String templateName,
            Map<String, Object> valuesMap) {
        try {
            Configuration configuration = new Configuration(FTL_CURRENT_VERSION);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate(templateName, formattedTemplate);
            configuration.setTemplateLoader(stringLoader);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template template = configuration.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(valuesMap, stringWriter);
            return stringWriter.toString();
        } catch (IOException | TemplateException ex) {
            log.error("Error caught while processing template named : {} Exception : {}",
                    templateName, ex);
        }
        return null;
    }
}
