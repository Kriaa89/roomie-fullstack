package com.backend.roomie.services;

import com.backend.roomie.models.Notification;
import com.backend.roomie.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    public Notification createNotification(Long recipientId, String content, Notification.NotificationType type) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(content)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public List<Notification> getUnreadNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(recipientId);
    }

    public List<Notification> getNotificationsByRecipientAndType(Long recipientId, Notification.NotificationType type) {
        return notificationRepository.findByRecipientIdAndType(recipientId, type);
    }

    public long getUnreadNotificationCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    public Notification markNotificationAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setIsRead(true);
                    return notificationRepository.save(notification);
                })
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    public List<Notification> markAllNotificationsAsRead(Long recipientId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsReadFalse(recipientId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        return notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllNotificationsForRecipient(Long recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        notificationRepository.deleteAll(notifications);
    }
}