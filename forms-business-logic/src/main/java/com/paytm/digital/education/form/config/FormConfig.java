package com.paytm.digital.education.form.config;

import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import paytm.auth.personaaclclient.infrastructure.security.CookieAuthenticationProvider;
import paytm.auth.personaaclclient.infrastructure.security.DomainUsernamePasswordAuthenticationProvider;

@EnableWebSecurity
@AllArgsConstructor
@Order(2)
public class FormConfig extends WebSecurityConfigurerAdapter {
    private LCPTokenAuthenticationProvider tokenAuthenticationProvider;

    private DomainUsernamePasswordAuthenticationProvider domainUsernamePasswordAuthenticationProvider;

    private CookieAuthenticationProvider cookieAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/formfbl/v1/paymentPosting").permitAll()
                .antMatchers(HttpMethod.GET, "/formfbl/v1/order/statuscheck").permitAll()
                .antMatchers(HttpMethod.POST, "/formfbl/v1/saveMerchantProductConfig").permitAll()
                .antMatchers(HttpMethod.GET, "/formfbl/v1/getMerchantProductConfig").permitAll()
                .antMatchers("/formfbl/form-data/**").permitAll()
                .antMatchers("/formfbl/v1/user/**").permitAll()
                .antMatchers("/formfbl/**").authenticated()
                .anyRequest()
                .permitAll();

        http.headers().frameOptions().disable();

        http.addFilterBefore(new FormAuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider)
                .authenticationProvider(cookieAuthenticationProvider);
    }
}
