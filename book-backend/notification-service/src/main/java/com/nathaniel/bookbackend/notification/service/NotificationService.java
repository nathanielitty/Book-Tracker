package com.nathaniel.bookbackend.notification.service;

import com.nathaniel.bookbackend.notification.model.Notification;
import com.nathaniel.bookbackend.notification.model.NotificationType;
import com.nathaniel.bookbackend.notification.repository.NotificationRepository;
import com.nathaniel.bookbackend.notification.events.UserActivityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(String userId, boolean unreadOnly, Pageable pageable) {
        if (unreadOnly) {
            return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false, pageable);
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    @Transactional
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false, Pageable.unpaged())
            .forEach(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
    }

    @KafkaListener(topics = "user-activity", groupId = "notification-service")
    public void handleUserActivity(UserActivityEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setResourceId(event.getBookId());

        switch (event.getActivityType()) {
            case "BOOK_ADDED":
                notification.setType(NotificationType.BOOK_ADDED);
                notification.setTitle("New Book Added");
                notification.setMessage("A new book has been added to your library");
                break;
            case "STATUS_UPDATED":
                notification.setType(NotificationType.READING_STATUS_CHANGED);
                notification.setTitle("Reading Status Updated");
                notification.setMessage("Your reading status has been updated");
                break;
            case "PROGRESS_UPDATED":
                notification.setType(NotificationType.READING_PROGRESS_UPDATED);
                notification.setTitle("Reading Progress Updated");
                notification.setMessage("Your reading progress has been updated");
                break;
            case "REVIEW_ADDED":
                notification.setType(NotificationType.REVIEW_ADDED);
                notification.setTitle("Review Added");
                notification.setMessage("Your review has been added");
                break;
            default:
                notification.setType(NotificationType.SYSTEM_NOTIFICATION);
                notification.setTitle("Activity Update");
                notification.setMessage("There has been an update to your activity");
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void createSystemNotification(String userId, String title, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(NotificationType.SYSTEM_NOTIFICATION);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    @Transactional
    public void createRecommendationNotification(String userId, String bookId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(NotificationType.RECOMMENDATION);
        notification.setTitle("Book Recommendation");
        notification.setMessage(message);
        notification.setResourceId(bookId);
        notificationRepository.save(notification);
    }
}
