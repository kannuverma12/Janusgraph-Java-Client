package com.paytm.digital.education.form.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import paytm.auth.personaaclclient.infrastructure.security.CookieAuthenticationProvider;

@EnableWebSecurity
@RequiredArgsConstructor
public class FormConfig extends WebSecurityConfigurerAdapter {
    private final LCPTokenAuthenticationProvider tokenAuthenticationProvider;
    private final CookieAuthenticationProvider cookieAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/formfbl/v1/orders").authenticated()
                .antMatchers("/formfbl/v1/orders/download").authenticated()
                .antMatchers("/formfbl/v1/orders/bulk-download").authenticated()
                .antMatchers("/formfbl/v1/download").authenticated()
                .anyRequest()
                .permitAll();

        http.headers().frameOptions().disable();

        http.addFilterBefore(new FormAuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/explore");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider)
                .authenticationProvider(cookieAuthenticationProvider);
    }
}
