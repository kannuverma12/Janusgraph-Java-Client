package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCommitUserInfo {

    private String userMobile;
    private String userEmail;
    private String userName;
}
