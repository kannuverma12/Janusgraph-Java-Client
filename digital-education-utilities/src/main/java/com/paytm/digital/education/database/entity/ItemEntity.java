package com.paytm.digital.education.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("item")
@CompoundIndex(def = "{'paytm_product_id':1, 'merchant_product_id':1}", unique = true, name = "item_unique")
public class ItemEntity {

    @Id
    @Field("_id")
    ObjectId id;

    @Field("merchant_id")
    private Long merchantId;

    @Field("merchant_product_id")
    private Long merchantProductId;

    @Field("paytm_product_id")
    private Long paytmProductId;

}
