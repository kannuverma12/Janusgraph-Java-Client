package com.paytm.digital.education.application.config.security;


import org.springframework.security.core.Authentication;
import paytm.auth.personaaclclient.infrastructure.security.ExternalServiceAuthenticator;


public interface AuthorizationService {

    public Long getMerchantId();

    boolean isAdmin();

    boolean isMerchant();

    Authentication authenticate(String token, Authentication authentication,
            ExternalServiceAuthenticator externalServiceAuthenticator);
}

