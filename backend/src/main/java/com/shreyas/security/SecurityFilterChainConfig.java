package com.shreyas.security;

import com.shreyas.jwt.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    @Qualifier("delegatedAuthEntryPoint")
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Below various filters are chained to form SecurityFilterChain e.g. csrf --> authorizeHttpRequests .....
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(HttpMethod.POST,
                                                "/api/v1/customers",
                                                "api/v1/auth/login",
                                                "api/v1/auth/signup"
                                        )
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/ping")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()        // The user should be authenticated for any request in the application.
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)    // Spring Security will never create an HttpSession, and it will never use it to obtain the Security Context.
                )
                .authenticationProvider(authenticationProvider)     // Here I'm using DaoAuthenticationProvider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Before UsernamePasswordAuthenticationFilter use JwtAuthenticationFilter
                .exceptionHandling(exceptionHandler -> exceptionHandler.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }
}