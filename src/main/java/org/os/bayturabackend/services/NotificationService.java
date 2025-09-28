package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.entities.*;
import org.os.bayturabackend.repositories.NotificationRepository;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(Long userId, String title, String content, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .user(user)
                .build();

        Notification saved = notificationRepository.save(notification);

//        messagingTemplate.convertAndSend(
//                "/queue/notifications/" + userId,
//                saved
//        );

        return saved;
    }

    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public long countUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public String deleteNotification(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isAdmin && !notification.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You are not allowed to delete this notification");
        }

        notificationRepository.delete(notification);
        return "Notification deleted";
    }

}
