package com.paytm.digital.education.form.handler;

import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;

@Slf4j
public abstract class BaseHandler<T> {

    private Class<T> modelClass;

    public BaseHandler() {
        this.modelClass = (Class<T>) ((ParameterizedType)
                this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void processMessage(String message) {
        try {
            T pojo = JsonUtils.fromJson(message, modelClass);
            this.handle(pojo);
        } catch (Exception e) {
            // TODO:- send metric
            log.error("MESSAGE_PROCESSING_FAILED RECORD: {}", message);
            log.error("ERROR MESSAGE: {}", e);
        }
    }

    public abstract void handle(T pojo) throws Exception;
}
