package com.paytm.digital.education.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    String[] toAddressList;
    String   fromAddress;
    String   subject;
    String   body;

}
