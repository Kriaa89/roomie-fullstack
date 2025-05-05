package com.backend.roomie.controllers;

import com.backend.roomie.dtos.NotificationDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Notification;
import com.backend.roomie.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<Notification> notifications = notificationService.getNotificationsByRecipientId(currentUser.getId());
        
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(notificationDtos);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<Notification> notifications = notificationService.getUnreadNotificationsByRecipientId(currentUser.getId());
        
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(notificationDtos);
    }

    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<NotificationDto> markNotificationAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        Notification notification = notificationService.getNotificationById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Verify the current user is the recipient
        if (!notification.getRecipientId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        notification.setRead(true);
        Notification updatedNotification = notificationService.updateNotification(notification);
        
        return ResponseEntity.ok(mapToDto(updatedNotification));
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<Void> markAllNotificationsAsRead(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        notificationService.markAllNotificationsAsRead(currentUser.getId());
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        Notification notification = notificationService.getNotificationById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Verify the current user is the recipient
        if (!notification.getRecipientId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}