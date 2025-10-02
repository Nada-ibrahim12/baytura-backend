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
import org.os.bayturabackend.controllers.AuthController;
import org.os.bayturabackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class AuthControllerTest {

        @Mock
        private AuthService authService;

        @InjectMocks
        private AuthController authController;

        private AuthResponseDTO mockResponse;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            mockResponse = AuthResponseDTO.builder()
                    .token("mockToken")
                    .userId(1L)
                    .username("mockUser")
                    .email("mock@email.com")
                    .role("ROLE_USER")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();

        }

        @Test
        void testRegisterCustomer() {
            RegisterUserDTO dto = new RegisterUserDTO("pass123");
            when(authService.registerCustomer(dto)).thenReturn(mockResponse);

            ResponseEntity<AuthResponseDTO> response = authController.registerCustomer(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("mockToken", response.getBody().getToken());
            verify(authService, times(1)).registerCustomer(dto);
        }

        @Test
        void testRegisterProvider() {
            RegisterProviderDTO dto = new RegisterProviderDTO("pass123");
            when(authService.registerProvider(dto)).thenReturn(mockResponse);

            ResponseEntity<AuthResponseDTO> response = authController.registerProvider(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("mockToken", response.getBody().getToken());
            verify(authService, times(1)).registerProvider(dto);
        }

        @Test
        void testRegisterAdmin() {
            RegisterUserDTO dto =  new RegisterUserDTO("pass123");
            when(authService.registerAdmin(dto)).thenReturn(mockResponse);

            ResponseEntity<AuthResponseDTO> response = authController.registerAdmin(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("mockToken", response.getBody().getToken());
            verify(authService, times(1)).registerAdmin(dto);
        }

        @Test
        void testLogin() {
            LoginRequestDTO dto = new LoginRequestDTO("user", "password");
            when(authService.login(dto)).thenReturn(mockResponse);

            ResponseEntity<AuthResponseDTO> response = authController.login(dto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("mockToken", response.getBody().getToken());
            verify(authService, times(1)).login(dto);
        }
    }


