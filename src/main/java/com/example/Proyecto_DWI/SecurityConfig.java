package com.example.Proyecto_DWI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())           // desactiva CSRF (no necesario en APIs REST)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()            // permite todas las peticiones sin login
            );
        return http.build();
    }
}
