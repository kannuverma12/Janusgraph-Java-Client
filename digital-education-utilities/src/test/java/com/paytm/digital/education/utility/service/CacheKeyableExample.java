package com.paytm.digital.education.utility.service;

import com.paytm.digital.education.advice.CacheKeyable;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

@AllArgsConstructor
public class CacheKeyableExample implements CacheKeyable {
    private int index;
    private ObjectId oid;
    private Long val;
    private String name;

    @Override
    public String[] cacheKeys() {
        return new String[]{
                String.valueOf(index),
                oid.toString(),
                String.valueOf(val),
                name
        };
    }

    public ObjectId getOid() {
        return oid;
    }
}
