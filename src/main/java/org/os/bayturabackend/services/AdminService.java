package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.entities.NotificationType;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public void approveProvider(Long providerId) {
        User user = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!(user instanceof Provider provider)) {
            throw new ForbiddenException("User is not a provider");
        }
        else{
            provider.setIsApproved(true);
            userRepository.save(provider);

// In-app notification
            notificationService.createNotification(
                    provider.getUserId(),
                    "Account Approved",
                    "Your provider account is now approved! Start adding your first property.",
                    NotificationType.ACCOUNT_APPROVED
            );

// Email notification
            String subject = "Welcome to BAYTAURA!";
            String content = "Hi " + provider.getFirstName() + ",\n\n" +
                    "Good news! Your provider account has been approved. " +
                    "You can now log in and start listing your properties on Baytaura.\n\n" +
                    "Happy hosting!\n" +
                    "— The Baytaura Team";

            emailService.sendEmail(provider.getEmail(), subject, content);

        }
    }
}
