package com.nathaniel.bookbackend.notification.controller;

import com.nathaniel.bookbackend.notification.model.Notification;
import com.nathaniel.bookbackend.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, unreadOnly, pageable));
    }

    @GetMapping("/users/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/system")
    public ResponseEntity<Void> createSystemNotification(
            @PathVariable String userId,
            @RequestParam String title,
            @RequestParam String message) {
        notificationService.createSystemNotification(userId, title, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/recommendations")
    public ResponseEntity<Void> createRecommendationNotification(
            @PathVariable String userId,
            @RequestParam String bookId,
            @RequestParam String message) {
        notificationService.createRecommendationNotification(userId, bookId, message);
        return ResponseEntity.ok().build();
    }
}
