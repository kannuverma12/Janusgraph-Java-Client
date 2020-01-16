package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.enums.CTAEntity;
import org.springframework.core.convert.converter.Converter;

public class CTAEntityConverter implements Converter<String, CTAEntity> {
    @Override
    public CTAEntity convert(String source) {
        return CTAEntity.valueOf(source.toUpperCase());
    }
}
