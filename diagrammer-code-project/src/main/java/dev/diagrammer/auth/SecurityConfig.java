package dev.diagrammer.auth;

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Public diagram URLs bypass login. The handler answers 404 until diagrams exist.
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/diagrams", "/diagrams/**").permitAll());
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer
                .loginView("/login", "/login")
                .anyRequest(AuthorizedUrl::authenticated));
        return http.build();
    }

    /**
     * Understands the {@code {noop}} prefix stored in the credentials file.
     * Hashing is intentionally later (S18).
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
