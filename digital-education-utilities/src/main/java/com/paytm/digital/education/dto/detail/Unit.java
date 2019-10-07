package com.paytm.digital.education.dto.detail;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Unit {

    private String      name;

    private List<Topic> topics;

}
