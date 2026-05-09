package edu.esi.ds.esiusuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final ApiKeyFilter apiKeyFilter;
        private final JwtAuthFilter jwtAuthFilter;

        public SecurityConfig(ApiKeyFilter apiKeyFilter, JwtAuthFilter jwtAuthFilter) {
                this.apiKeyFilter = apiKeyFilter;
                this.jwtAuthFilter = jwtAuthFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                // 1. Configuración general: desactivar CSRF y establecer sesiones sin estado
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 2. Configuración de autorización de rutas
                                .authorizeHttpRequests(auth -> auth
                                                // Rutas públicas
                                                .requestMatchers(
                                                                "/users/register",
                                                                "/users/login",
                                                                "/users/forgot-password",
                                                                "/users/reset-password")
                                                .permitAll()

                                                // Rutas exclusivas para el servicio esientradas
                                                .requestMatchers("/users/token/**").hasRole("SERVICE")

                                                // Rutas exclusivas para usuarios registrados
                                                .requestMatchers(
                                                                "/users/removeUser",
                                                                "/users/change-password")
                                                .hasRole("USER")

                                                // Cualquier otra ruta requiere autenticación
                                                .anyRequest().authenticated())

                                // 3. Configuración del orden de los filtros
                                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(jwtAuthFilter, ApiKeyFilter.class)

                                .build();
        }
}