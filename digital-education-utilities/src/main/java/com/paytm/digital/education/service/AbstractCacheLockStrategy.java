package com.paytm.digital.education.service;

import com.paytm.digital.education.exception.SerializationException;
import com.paytm.education.logger.Logger;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;

public abstract class AbstractCacheLockStrategy implements CacheLockStrategy {
    protected String serializeData(Object o, String key, Logger logger) {
        try {
            return toHexString(o);
        } catch (IOException e) {
            logger.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, o);
            throw new SerializationException(e);
        }
    }

    protected Object deSerializeData(String data, String key, Logger logger) {
        try {
            return fromHexString(data);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, data);
            throw new SerializationException(e);
        }
    }
}
