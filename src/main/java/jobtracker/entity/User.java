package jobtracker.entity;

import jakarta.persistence.*;

/**
 * PROBLEM: Store user accounts for authentication — each JobApplication belongs to a User.
 *
 * APPROACH: Store username + a BCrypt-hashed password (never plain text). Password
 * hashing happens in the service layer, not here — this class just holds the data.
 *
 * WHY IT WORKS: BCrypt hashing is one-way; even a database leak never exposes real
 * passwords. Login re-hashes the entered password and compares hashes, not raw text.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // stores the BCrypt HASH, never the real password

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}