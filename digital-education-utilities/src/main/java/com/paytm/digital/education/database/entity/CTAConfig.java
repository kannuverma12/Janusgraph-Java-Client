package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CTAType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
public class CTAConfig implements Serializable {
    private static final long serialVersionUID = 6854394100027705957L;

    @JsonProperty("cta_types")
    @Field("cta_types")
    private List<CTAType> ctaTypes = emptyList();
}
