package com.TP_Metamapa.Configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // paginas publicas (permitAll)
                        .requestMatchers("/actuator/**", "/actuator/prometheus").permitAll()
                        .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/navegar", "/estadisticas", "/ver-hecho/{id}", "/csv").permitAll()
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // administrador -> admin_client_role
                        .requestMatchers("/admin/**").hasRole("admin_client_role") // Protege todo bajo /admin


                        // usuarios que iniciaron sesion
                        .requestMatchers(HttpMethod.GET, "/crear-hecho", "/solicitarEliminacion/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/crear-hecho", "/crearSolicitud").authenticated()

                        // cualquier otra petición no definida arriba requiere autenticacion
                        .anyRequest().authenticated()
                )
                // config de Login y Logout
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll()
                    );

            return http.build();
        }
}