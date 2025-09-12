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

    public void approveProvider(Long providerId) {
        User user = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!(user instanceof Provider provider)) {
            throw new ForbiddenException("User is not a provider");
        }
        else{
            provider.setIsApproved(true);
            userRepository.save(provider);

            notificationService.createNotification(
                    provider.getUserId(),
                    "Account Approved",
                    "Congratulations! Your provider account has been approved.",
                    NotificationType.ACCOUNT_APPROVED
            );
        }
    }
}
