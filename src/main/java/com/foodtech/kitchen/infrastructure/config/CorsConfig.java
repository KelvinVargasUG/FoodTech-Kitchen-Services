package com.foodtech.kitchen.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para desarrollo.
 * ADVERTENCIA: Esta configuración es PERMISIVA y está diseñada SOLO para desarrollo.
 * En producción debes restringir los orígenes, métodos y headers permitidos.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite peticiones desde cualquier origen (para desarrollo)
        // En producción, especifica los dominios exactos: config.setAllowedOrigins(Arrays.asList("https://tu-dominio.com"));
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Permite credenciales (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Permite todos los métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Permite todos los headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Expone estos headers en la respuesta para que el frontend pueda leerlos
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Page-Number"
        ));
        
        // Tiempo máximo que el navegador puede cachear la respuesta de preflight (OPTIONS)
        config.setMaxAge(3600L); // 1 hora
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a todos los endpoints
        
        return new CorsFilter(source);
    }
}
