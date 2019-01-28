package com.paytm.digital.education.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig extends AbstractFactoryBean<ObjectMapper> {

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    protected ObjectMapper createInstance() throws Exception {
        return new ObjectMapper();
    }

}
