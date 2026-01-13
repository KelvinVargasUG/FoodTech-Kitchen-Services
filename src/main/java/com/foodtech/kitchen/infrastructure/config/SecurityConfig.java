package com.foodtech.kitchen.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuración de seguridad para desarrollo.
 * Configura headers de seguridad y deshabilita CSRF para facilitar pruebas.
 * ADVERTENCIA: En producción debes habilitar CSRF y ajustar las políticas de seguridad.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para desarrollo (necesario para POST/PUT/DELETE sin token)
            // En producción con frontend SPA, configura CSRF con tokens
            .csrf(csrf -> csrf.disable())
            
            // Configurar autorización
            .authorizeHttpRequests(auth -> auth
                // Permitir todos los endpoints de la API (para desarrollo)
                // En producción, especifica roles: .requestMatchers("/api/**").hasRole("USER")
                .requestMatchers("/api/**").permitAll()
                // Cualquier otra petición requiere autenticación (cuando la implementes)
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
            );
        
        return http.build();
    }
}
