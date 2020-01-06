package com.paytm.digital.education.dto.detail;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Unit implements Serializable {
    private static final long serialVersionUID = -4967792475312723297L;

    private String      name;

    private List<Topic> topics;

}
