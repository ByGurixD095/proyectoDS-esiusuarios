package edu.esi.ds.esiusuarios.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ApiKeyFilter apiKeyFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Rutas públicas — cualquiera puede llamarlas
                        .requestMatchers(
                                "/users/register",
                                "/users/login",
                                "/users/forgot-password",
                                "/users/reset-password")
                        .permitAll()

                        // Rutas solo para esientradas (ROLE_SERVICE via API Key)
                        .requestMatchers(
                                "/users/token/**")
                        .hasRole("SERVICE")

                        // Rutas solo para el usuario autenticado (ROLE_USER via JWT)
                        .requestMatchers(
                                "/users/removeUser",
                                "/users/change-password")
                        .hasRole("USER")

                        // Cualquier otra ruta requiere estar autenticado
                        .anyRequest().authenticated())
                // Primero la API Key, luego el JWT
                .addFilterBefore(apiKeyFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter,
                        ApiKeyFilter.class)
                .build();
    }
}