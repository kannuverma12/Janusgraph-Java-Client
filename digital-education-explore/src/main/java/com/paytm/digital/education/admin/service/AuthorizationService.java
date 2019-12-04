package com.paytm.digital.education.admin.service;

import org.springframework.security.core.Authentication;
import paytm.auth.personaaclclient.infrastructure.security.ExternalServiceAuthenticator;

public interface AuthorizationService {

    public Long getMerchantId();

    public Long getUserId();

    boolean isAdmin();

    boolean isMerchant();

    Authentication authenticate(String token, Authentication authentication,
            ExternalServiceAuthenticator externalServiceAuthenticator);
}
