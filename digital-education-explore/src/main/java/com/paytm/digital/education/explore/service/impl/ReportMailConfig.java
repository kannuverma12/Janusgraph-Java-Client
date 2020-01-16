package com.paytm.digital.education.explore.service.impl;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Data
@Configuration
public class ReportMailConfig {

    @Value("${onboard.report.receiver}")
    private String receiver;

    @Value("${onboard.report.from}")
    private String from;

    @Value("${onboard.report.subject}")
    private String subject;

    public String[] toAddresses() {
        if (StringUtils.isNotBlank(this.receiver)) {
            return this.receiver.split(",");
        }
        return new String[]{};
    }
}
