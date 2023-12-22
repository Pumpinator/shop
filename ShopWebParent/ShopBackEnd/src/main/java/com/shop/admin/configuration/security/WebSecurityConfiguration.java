package com.shop.admin.configuration.security;

import com.shop.admin.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Bean
    public ShopUserDetailsService shopUserDetailsService() {
        return new ShopUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(shopUserDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/img/**", "/css/**", "/js/**", "/webjars/**")
                    .permitAll();
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/users/**", "/settings/**")
                    .hasAuthority("Admin")
                    .requestMatchers("/categories/**", "/brands/**", "/articles/**", "/menus/**")
                    .hasAnyAuthority("Admin", "Editor")
                    .requestMatchers("/products/**")
                    .hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
                    .requestMatchers("/customers/**", "/shipping/**", "/reports/**")
                    .hasAnyAuthority("Admin", "Salesperson")
                    .requestMatchers("/orders/**")
                    .hasAnyAuthority("Admin", "Salesperson", "Shipper")
                    .anyRequest()
                    .authenticated();
        });
        http.formLogin(httpSecurityFormLoginConfigurer -> {
            httpSecurityFormLoginConfigurer
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll();
        });
        http.rememberMe(httpSecurityRememberMeConfigurer -> {
            httpSecurityRememberMeConfigurer
                    .key("AbcdEfghIjklmNopQrsTuvXyz_0123456789");
            httpSecurityRememberMeConfigurer.tokenValiditySeconds(7 * 24 * 60 * 60);
        });
        http.logout(httpSecurityLogoutConfigurer -> {
            httpSecurityLogoutConfigurer
                    .permitAll();
        });
        http.headers(httpSecurityHeadersConfigurer -> {
            httpSecurityHeadersConfigurer
                    .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin());
        });
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
}