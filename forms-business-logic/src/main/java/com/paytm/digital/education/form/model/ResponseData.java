package com.paytm.digital.education.form.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseData<T> {
    private long count;
    private List<T> data;

    public ResponseData(List<T> data) {
        this.count = data.size();
        this.data = data;
    }
}
