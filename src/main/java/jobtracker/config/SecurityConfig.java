package jobtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}