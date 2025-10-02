package org.os.bayturabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.os.bayturabackend.DTOs.ChangeStatusDTO;
import org.os.bayturabackend.controllers.AdminController;
import org.os.bayturabackend.entities.NotificationType;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.ProviderStatus;
import org.os.bayturabackend.services.AdminService;
import org.os.bayturabackend.services.EmailService;
import org.os.bayturabackend.services.NotificationService;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.context.Context;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChangeStatusAccepted() {
        // Arrange
        Long providerId = 1L;
        ChangeStatusDTO body = new ChangeStatusDTO();
        body.setStatus("ACCEPTED");

        doNothing().when(adminService).changeStatus(providerId, "ACCEPTED");

        // Act
        ResponseEntity<String> response = adminController.changeStatus(providerId, body);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Provider status updated", response.getBody());

        verify(adminService, times(1)).changeStatus(providerId, "ACCEPTED");
    }

    @Test
    void testChangeStatusRejected() {
        // Arrange
        Long providerId = 2L;
        ChangeStatusDTO body = new ChangeStatusDTO();
        body.setStatus("REJECTED");

        doNothing().when(adminService).changeStatus(providerId, "REJECTED");

        // Act
        ResponseEntity<String> response = adminController.changeStatus(providerId, body);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Provider status updated", response.getBody());

        verify(adminService, times(1)).changeStatus(providerId, "REJECTED");
    }
}

