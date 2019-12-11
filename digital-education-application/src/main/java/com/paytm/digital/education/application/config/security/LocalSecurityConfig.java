package com.paytm.digital.education.application.config.security;


import com.paytm.digital.education.application.config.filter.PersonaAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import paytm.auth.personaaclclient.infrastructure.security.CookieAuthenticationProvider;


@Profile({"staging", "production"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(99)
@RequiredArgsConstructor
public class LocalSecurityConfig extends WebSecurityConfigurerAdapter {
    private final LCPTokenAuthenticationProvider tokenAuthenticationProvider;
    private final CookieAuthenticationProvider   cookieAuthenticationProvider;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(cookieAuthenticationProvider)
                .authenticationProvider(tokenAuthenticationProvider)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/explore/admin/entity/v1/**").authenticated()
                .anyRequest().permitAll();

        http.headers().frameOptions().disable();

        http.addFilterBefore(new PersonaAuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("^(?!/explore/admin/entity/v1/).*");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider)
                .authenticationProvider(cookieAuthenticationProvider);
    }
}
