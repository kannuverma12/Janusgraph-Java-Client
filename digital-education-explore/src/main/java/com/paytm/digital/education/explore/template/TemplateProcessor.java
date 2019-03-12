package com.paytm.digital.education.explore.template;

import static com.paytm.digital.education.explore.constants.ExploreConstants.FTL_CURRENT_VERSION;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TemplateProcessor {

    public String processTemplate(String formattedTemplate, String templateName,
            Object object, String objectName) {
        try {
            Configuration configuration = new Configuration(FTL_CURRENT_VERSION);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate(templateName, formattedTemplate);
            configuration.setTemplateLoader(stringLoader);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put(objectName, object);
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
