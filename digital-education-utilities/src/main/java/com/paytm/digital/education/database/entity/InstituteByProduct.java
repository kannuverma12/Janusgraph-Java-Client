package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.InstituteLiveStatus;
import com.paytm.digital.education.enums.Product;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Document("institute_by_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@CompoundIndex(def = "{'explore_institute_id':1, 'product':1}", unique = true, name = "institute_by_product_unique")
public class InstituteByProduct implements Serializable {
    private static final long serialVersionUID = 5924569942703014365L;

    @Id
    @JsonIgnore
    private ObjectId id;

    @Field("explore_institute_id")
    private long exploreInstituteId;

    @Field("product")
    private Product product;

    @Field("institute_live_status")
    private InstituteLiveStatus instituteLiveStatus;

    @Field("order")
    private int order;

    @Field("created_at")
    @JsonIgnore
    private Date createdAt;

    @Field("updated_at")
    @JsonIgnore
    private Date updatedAt;
}
