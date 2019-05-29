package com.paytm.digital.education.form.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadOrder {
    private String userId;
    private Long orderId;
}
