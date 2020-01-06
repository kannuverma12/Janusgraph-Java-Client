package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.CTAEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CTAConfig ctaConfig;

    @Override
    public CTAConfig getCTAConfig() {
        return ctaConfig;
    }

    @Override
    public void setCTAConfig(CTAConfig ctaConfig) {
        this.ctaConfig = ctaConfig;
    }

    public EducationEntityCTAConfig(CTAEntity ctaEntity, CTAConfig cTAConfig) {
        this.ctaEntity = ctaEntity;
        this.ctaConfig = cTAConfig;
    }
}
