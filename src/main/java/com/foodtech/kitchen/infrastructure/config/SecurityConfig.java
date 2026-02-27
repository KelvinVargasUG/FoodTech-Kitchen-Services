package com.foodtech.kitchen.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.http.HttpStatus;
import com.foodtech.kitchen.infrastructure.security.JwtAuthenticationFilter;
import com.foodtech.kitchen.infrastructure.security.JwtTokenValidator;

/**
 * Configuración de seguridad para desarrollo.
 * Configura headers de seguridad y deshabilita CSRF para facilitar pruebas.
 * ADVERTENCIA: En producción debes habilitar CSRF y ajustar las políticas de seguridad.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            // Deshabilitar CSRF para desarrollo (necesario para POST/PUT/DELETE sin token)
            // En producción con frontend SPA, configura CSRF con tokens
            .csrf(csrf -> csrf.disable())
            
            // Configurar autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            
            // Sin sesión (stateless) - típico para APIs REST
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar headers de seguridad
            .headers(headers -> headers
                // X-Frame-Options: protege contra clickjacking
                .frameOptions(frame -> frame.deny())
                
                // X-Content-Type-Options: previene MIME sniffing
                .contentTypeOptions(contentType -> {})
                
                // X-XSS-Protection: protección básica contra XSS (legacy, pero no hace daño)
                .xssProtection(xss -> {})
                
                // Referrer-Policy: controla qué información se envía en el header Referer
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                
                // Content Security Policy (CSP) - deshabilitado para desarrollo
                // En producción, configura una política estricta
                // .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        return new JwtAuthenticationFilter(jwtTokenValidator);
    }
}
