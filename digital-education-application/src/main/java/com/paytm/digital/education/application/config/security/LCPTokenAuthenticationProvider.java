package com.paytm.digital.education.application.config.security;


import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import paytm.auth.personaaclclient.infrastructure.models.RequestPreAuthenticatedAuthenticationToken;
import paytm.auth.personaaclclient.infrastructure.security.ExternalServiceAuthenticator;

@Component
public class LCPTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthorizationService authorizationService;

    private ExternalServiceAuthenticator externalServiceAuthenticator;

    @Autowired
    public LCPTokenAuthenticationProvider(
            ExternalServiceAuthenticator externalServiceAuthenticator) {
        this.externalServiceAuthenticator = externalServiceAuthenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Optional<String> token = (Optional) authentication.getPrincipal();
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }
        return authorizationService
                .authenticate(token.get(), authentication, externalServiceAuthenticator);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RequestPreAuthenticatedAuthenticationToken.class);
    }

}
