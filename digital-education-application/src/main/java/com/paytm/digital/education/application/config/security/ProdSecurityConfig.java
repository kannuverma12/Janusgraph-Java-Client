package com.paytm.digital.education.application.config.security;

import com.paytm.digital.education.application.constant.ProfileConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import paytm.auth.personaaclclient.infrastructure.security.AuthenticationFilter;
import paytm.auth.personaaclclient.infrastructure.security.CookieAuthenticationProvider;
import paytm.auth.personaaclclient.infrastructure.security.DomainUsernamePasswordAuthenticationProvider;
import paytm.auth.personaaclclient.infrastructure.security.TokenAuthenticationProvider;

@Profile({ProfileConstants.PRODUCTION})
@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProdSecurityConfig extends BaseSecurityConfig {

    @Autowired
    TokenAuthenticationProvider tokenAuthenticationProvider;

    @Autowired
    DomainUsernamePasswordAuthenticationProvider domainUsernamePasswordAuthenticationProvider;

    @Autowired
    CookieAuthenticationProvider cookieAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/ping", "/health")
                .permitAll().anyRequest().authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(unauthorizedEntryPoint());
        http.addFilterBefore(new AuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider)
                .authenticationProvider(domainUsernamePasswordAuthenticationProvider)
                .authenticationProvider(cookieAuthenticationProvider);
    }

}
