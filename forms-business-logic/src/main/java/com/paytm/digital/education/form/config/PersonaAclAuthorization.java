package com.paytm.digital.education.form.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import paytm.auth.personaaclclient.domain.DomainUser;
import paytm.auth.personaaclclient.infrastructure.security.ExternalServiceAuthenticator;

@Service
public class PersonaAclAuthorization implements AuthorizationService {

    @Value("${acl.endpoint}")
    private String aclUrl;

    @Value("${acl.is_to_authorize}")
    private String isToAuthorize;

    private boolean isInitialized = false;
    private static final Logger LOGGER =
            LogManager.getLogger(PersonaAclAuthorization.class);

    @Override
    public boolean isAdmin() {
        try {
            boolean result = isAdmin(getLoggedInUser());
            return isAuthorizationTurnedOff() || result;
        } catch (Exception e) {
            LOGGER.error("FAILED:isAdmin:", e);
            return isAuthorizationTurnedOff();
        }
    }

    @Override
    public boolean isMerchant() {
        try {
            boolean result = isMerchant(getLoggedInUser());
            return isAuthorizationTurnedOff() || result;
        } catch (Exception e) {
            LOGGER.error("FAILED:isMerchant:", e);
            return isAuthorizationTurnedOff();
        }
    }

    @Override
    public Long getMerchantId() {

        DomainUser domainUser = getLoggedInUser();

        if (domainUser == null || !isMerchant(domainUser)) {
            return null;
        }

        return domainUser.getMerchantId();
    }

    private DomainUser getLoggedInUser() {
        Object domainUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (domainUser instanceof DomainUser) {
            return (DomainUser) domainUser;
        } else {
            LOGGER.error("NO_DOMAIN_USER: " + domainUser);
            return null;
        }
    }

    private boolean isAdmin(DomainUser domainUser) {

        boolean isAdmin = domainUser != null ? domainUser.isAdmin() : false;
        // TODO: Null check.
        LOGGER.debug("Checking user with merchant id " + domainUser.getMerchantId()
                + "and user id " + domainUser.getUserId() + " is admin or not. isAdmin value is "
                + isAdmin);

        return domainUser != null ? domainUser.isAdmin() : false;
    }

    private boolean isMerchant(DomainUser domainUser) {
        boolean isMerchant = domainUser != null ? domainUser.isMerchant() : false;

        LOGGER.debug("Checking user with merchant id " + domainUser.getMerchantId()
                + "and user id " + domainUser.getUserId() + "is merchant or not. isMerchant value is "
                + isMerchant);
        return isMerchant;
    }

    private boolean isAuthorizationTurnedOff() {

        boolean isToSkipAuthorization =
                isToAuthorize != null && isToAuthorize.equalsIgnoreCase("false");
        if (!isInitialized) {
            LOGGER.info("Authorization flag value is:" + isToAuthorize);
            isInitialized = true;
        }

        return isToSkipAuthorization;
    }

    @Cacheable(value = CacheConfig.PERSONA_CACHE, key = "#token", unless = "#result == null")
    public Authentication authenticate(String token,
                                       Authentication authentication,
                                       ExternalServiceAuthenticator externalServiceAuthenticator) {
        return externalServiceAuthenticator.authenticate(token, authentication);
    }
}
