package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.entities.Notification;
import org.os.bayturabackend.entities.NotificationType;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam NotificationType type
    ) {
        Notification notification = notificationService.createNotification(userId, title, content, type);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(Authentication auth) {
        User user = (User) auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication auth) {
        User user = (User) auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(Authentication auth) {
        User user = (User) auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(Authentication authentication, @PathVariable Long id) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.deleteNotification(id, currentUser);
        return ResponseEntity.ok("Notification deleted successfully.");
    }
}
