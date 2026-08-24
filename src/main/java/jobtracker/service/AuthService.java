package jobtracker.service;

import jobtracker.entity.User;
import jobtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * PROBLEM: Handle user registration — check for duplicates, hash passwords, save users.
 *
 * APPROACH: Depend on UserRepository and PasswordEncoder via constructor injection
 * (Dependency Inversion again — this class doesn't know or care HOW hashing works,
 * just that PasswordEncoder can do it).
 *
 * WHY IT WORKS: Checking findByUsername BEFORE saving prevents duplicate accounts.
 * Hashing happens here, in the service layer — never in the entity or controller.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(username, email, hashedPassword);

        return userRepository.save(newUser);
    }
}