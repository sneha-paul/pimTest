package com.bigname.pim.config;

import com.m7.xtreme.xcore.config.AjaxAwareAuthenticationEntryPoint;
import com.m7.xtreme.xcore.config.BaseSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@EnableWebSecurity
@ComponentScan
public class PIMSecurityConfig extends BaseSecurityConfig {

    @Autowired
    PimAuthenticationSuccessHandler pimAuthenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/actuator/health").permitAll()//.hasRole("ADMIN")
                .antMatchers( "/pim/user/**", "/forgotPassword").permitAll()
                .antMatchers("/pim/feeds/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
//                .successHandler(pimAuthenticationSuccessHandler)
                .loginPage("/login").permitAll()
                .and()
                .logout()
                //.logoutSuccessHandler(pimLogoutSuccessHandler)
                .permitAll()
                .and().exceptionHandling().authenticationEntryPoint(new AjaxAwareAuthenticationEntryPoint("/login"))
                .and().csrf().disable();
    }
}