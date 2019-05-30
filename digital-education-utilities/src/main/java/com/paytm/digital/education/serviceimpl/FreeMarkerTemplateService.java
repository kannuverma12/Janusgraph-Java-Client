package com.paytm.digital.education.serviceimpl;

import com.paytm.digital.education.service.TemplateService;
import com.paytm.digital.education.utility.JsonUtils;
import freemarker.core.XHTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;

public class FreeMarkerTemplateService implements TemplateService {

    private final Logger logger = LoggerFactory.getLogger(FreeMarkerTemplateService.class);

    private Configuration cfg;

    public FreeMarkerTemplateService() {
        /* ------------------------------------------------------------------------ */
        /* You should do this ONLY ONCE in the whole application life-cycle:        */

        /* Create and adjust the configuration singleton */
        cfg = new Configuration(Configuration.VERSION_2_3_27);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setOutputFormat(XHTMLOutputFormat.INSTANCE);
    }

    public String renderTemplate(String templateFile, Object dataModel) {
        try {
            /* Get the template (uses cache internally) */
            Template template = cfg.getTemplate(templateFile);

            /* Merge data-model with template */
            Writer out = new StringWriter();
            template.process(dataModel, out);
            return out.toString();

        } catch (Exception e) {
            logger.error("Exception in template(" + templateFile
                    + ") rendering: " + JsonUtils.toJson(dataModel), e);
            return null;
        }
    }


}
