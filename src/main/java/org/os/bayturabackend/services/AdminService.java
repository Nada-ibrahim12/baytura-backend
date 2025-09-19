package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.entities.NotificationType;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.ProviderStatus;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;



@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public void changeStatus(Long providerId, String status) {
        User user = userRepository.findById(providerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        if (!(user instanceof Provider provider)) {
            throw new ForbiddenException("User is not a provider");
        }

        if (provider.getStatus() != ProviderStatus.PENDING) {
            throw new ForbiddenException("Provider is not in pending status");
        }

        ProviderStatus newStatus;
        try {
            newStatus = ProviderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        switch (newStatus) {
            case ACCEPTED -> {
                provider.setStatus(ProviderStatus.ACCEPTED);
                userRepository.save(provider);

                // ? In-app notification
                notificationService.createNotification(
                        provider.getUserId(),
                        "Account Approved",
                        "Your provider account is now approved! Start adding your first property.",
                        NotificationType.ACCOUNT_APPROVED
                );

                // ? Email notification
                Context context = new Context();
                context.setVariable("providerName", provider.getFirstName());

                emailService.sendEmailFromTemplate(
                        provider.getEmail(),
                        "Your Provider Account Has Been Approved 🎉",
                        "emails/provider-status/provider_accepted",
                        context
                );
            }
            case REJECTED -> {
                provider.setStatus(ProviderStatus.REJECTED);
                userRepository.save(provider);

                // ? In-app notification
                notificationService.createNotification(
                        provider.getUserId(),
                        "Account Rejected",
                        "Your provider account is now rejected! Please try again later.",
                        NotificationType.ACCOUNT_REJECTED
                );

                // ? Email notification
                Context context = new Context();
                context.setVariable("providerName", provider.getFirstName());

                emailService.sendEmailFromTemplate(
                        provider.getEmail(),
                        "Your Provider Account Has Been Rejected ❌",
                        "emails/provider-status/provider_rejected",
                        context
                );
            }
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
