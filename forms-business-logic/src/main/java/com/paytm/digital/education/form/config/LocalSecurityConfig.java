package com.paytm.digital.education.form.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import paytm.auth.personaaclclient.domain.DomainUser;


@Profile({"local", "test", "dev", "default"})
@Configuration
@EnableWebSecurity
public class LocalSecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().anyRequest().authenticated()
                .and().anonymous().principal(DomainUser.getAnonymousUser());
    }

    @Bean
    public AuthenticationTrustResolver trustResolver() {
        return new AuthenticationTrustResolver() {

            @Override
            public boolean isRememberMe(final Authentication authentication) {
                return false;
            }

            @Override
            public boolean isAnonymous(final Authentication authentication) {
                return false;
            }
        };
    }

}
