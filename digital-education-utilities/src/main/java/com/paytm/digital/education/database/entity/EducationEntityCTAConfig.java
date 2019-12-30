package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.CTAEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@Document("education_entity_cta_config")
@NoArgsConstructor
public class EducationEntityCTAConfig implements Serializable, CTAConfigHolder {
    private static final long serialVersionUID = 5841226463951268056L;

    @Id
    private String id;

    @Field("cta_entity")
    private CTAEntity ctaEntity;

    @Field("cta_config")
    private CTAConfig cTAConfig;

    public EducationEntityCTAConfig(CTAEntity ctaEntity, CTAConfig cTAConfig) {
        this.ctaEntity = ctaEntity;
        this.cTAConfig = cTAConfig;
    }
}
