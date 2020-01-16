package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CTAType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.LinkedHashSet;

import static com.google.common.collect.Sets.newLinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CTAConfig implements Serializable {
    private static final long serialVersionUID = 6854394100027705957L;

    @JsonProperty("cta_types")
    @Field("cta_types")
    private LinkedHashSet<CTAType> ctaTypes = newLinkedHashSet();
}
