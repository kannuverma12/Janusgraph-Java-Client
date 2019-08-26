package com.paytm.digital.education.explore.database.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
public class InstiPaytmKeys extends PaytmKeys {

    @Field("pid")
    private Long pid;

    @Field("mid")
    private Long mid;

}
