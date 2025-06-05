package com.nathaniel.bookbackend.repository;

import com.nathaniel.bookbackend.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AppUser entity following Netflix data access patterns
 * Includes custom queries for performance optimization
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    
    /**
     * Find user by username (case-insensitive)
     */
    @Query("SELECT u FROM AppUser u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<AppUser> findByUsername(@Param("username") String username);
    
    /**
     * Find user by email (case-insensitive)
     */
    @Query("SELECT u FROM AppUser u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<AppUser> findByEmail(@Param("email") String email);
    
    /**
     * Find user by username or email (case-insensitive)
     */
    @Query("SELECT u FROM AppUser u WHERE LOWER(u.username) = LOWER(:identifier) OR LOWER(u.email) = LOWER(:identifier)")
    Optional<AppUser> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * Check if username exists (case-insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * Check if email exists (case-insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * Update last login timestamp
     */
    @Modifying
    @Query("UPDATE AppUser u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("loginTime") Instant loginTime);
    
    /**
     * Find all enabled users
     */
    @Query("SELECT u FROM AppUser u WHERE u.enabled = true")
    java.util.List<AppUser> findAllEnabled();
    
    /**
     * Count total active users
     */
    @Query("SELECT COUNT(u) FROM AppUser u WHERE u.enabled = true")
    long countActiveUsers();
}