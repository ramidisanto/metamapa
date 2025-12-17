package com.TP_Metamapa.Configuracion;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CRÍTICO: Esto ayuda a manejar la expiración de sesiones
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Páginas públicas
                        .requestMatchers("/actuator/**", "/rate-limit-error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/navegargraphql", "/navegar/**", "/estadisticas", "/ver-hecho/{id}", "/csv").permitAll()
                        .requestMatchers("/auth/iniciar-sesion", "/auth/login", "/auth/register").permitAll()
                        .requestMatchers("/error/**").permitAll()

                        // Admin
                        .requestMatchers("/admin/**").hasRole("admin_client_role")

                        // Usuarios autenticados
                        .requestMatchers(HttpMethod.GET, "/crear-hecho", "/solicitarEliminacion/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/crear-hecho", "/crearSolicitud").authenticated()

                        // Resto requiere autenticación
                        .anyRequest().authenticated()
                )
                // CRÍTICO: Configuración de sesiones mejorada
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/auth/login?session=expired")
                        .maximumSessions(5) // Permite múltiples sesiones por usuario
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/auth/login?session=expired")
                )
                // Manejo de excepciones mejorado
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String requestURI = request.getRequestURI();

                            // Si es una petición AJAX
                            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Sesión expirada. Por favor, inicia sesión nuevamente.\"}");
                            } else {
                                // Redirige al login
                                response.sendRedirect("/auth/login?returnUrl=" + requestURI);
                            }
                        })
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}