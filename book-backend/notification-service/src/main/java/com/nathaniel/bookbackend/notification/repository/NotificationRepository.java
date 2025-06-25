package com.nathaniel.bookbackend.notification.repository;

import com.nathaniel.bookbackend.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Page<Notification> findByUserIdAndReadOrderByCreatedAtDesc(String userId, boolean read, Pageable pageable);
    long countByUserIdAndRead(String userId, boolean read);
}
