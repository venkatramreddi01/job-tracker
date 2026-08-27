package jobtracker.config;

import jobtracker.security.JwtAuthFilter;
import jobtracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * PROBLEM: Provide a reusable BCrypt password encoder for the whole app.
 *
 * APPROACH: Define it as a Spring @Bean, so any class (like our future AuthService)
 * can just ask Spring for it via dependency injection, instead of creating
 * "new BCryptPasswordEncoder()" manually everywhere it's needed.
 *
 * WHY IT WORKS: This is Dependency Inversion in practice (from our LLD session) —
 * other classes depend on the PasswordEncoder INTERFACE, not a specific implementation.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil); // built by hand, NOT a Spring-managed bean

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}