package org.os.bayturabackend;

import lombok.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.os.bayturabackend.DTOs.AuthResponseDTO;
import org.os.bayturabackend.DTOs.LoginRequestDTO;
import org.os.bayturabackend.DTOs.RegisterProviderDTO;
import org.os.bayturabackend.DTOs.RegisterUserDTO;
import org.os.bayturabackend.controllers.AdminController;
import org.os.bayturabackend.controllers.AuthController;
import org.os.bayturabackend.services.AdminService;
import org.os.bayturabackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AdminControllerTest {
    @Mock
    private AdminService adminservice;
    @InjectMocks
    private AdminController adminController;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
 @Test
    void testApproveProvider() {
        // Arrange
        Long providerId = 1L;

        // Act
        ResponseEntity<String> response = adminController.approveProvider(providerId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Provider approved", response.getBody());

        // Verify that adminService.approveProvider was called once with the right id
        verify(adminservice, times(1)).approveProvider(providerId);
    }
}
