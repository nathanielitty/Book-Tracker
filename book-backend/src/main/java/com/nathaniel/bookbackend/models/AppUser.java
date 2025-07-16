package com.nathaniel.bookbackend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

/**
 * Entity representing a user in the application
 * Following Netflix entity design patterns with proper auditing
 */
@Entity
@Table(name = "app_user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password") // Never include password in toString
@EqualsAndHashCode(of = "id") // Only use ID for equals/hashCode
public class AppUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;
    
    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;
    
    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserBook> userBooks = new ArrayList<>();
    
    /**
     * Updates the last login timestamp
     */
    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }
}
