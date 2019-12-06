package com.paytm.digital.education.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Topic implements Serializable {
    private static final long serialVersionUID = 2870908914765544086L;
    private String name;

}
