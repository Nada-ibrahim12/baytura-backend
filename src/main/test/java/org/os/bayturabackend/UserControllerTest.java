package org.os.bayturabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.controllers.UserController;
import org.os.bayturabackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private UserResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User
        mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(1L);

        // Mock Authentication
        when(authentication.getPrincipal()).thenReturn(mockUser);

        // Mock Response
        mockResponse = new UserResponseDTO();
        mockResponse.setUsername("testUser");
        mockResponse.setEmail("test@example.com");
        mockResponse.setFirstName("John");
        mockResponse.setLastName("Doe");
        mockResponse.setPhone("123456789");
        mockResponse.setRole("CUSTOMER");
        mockResponse.setProfilePictureUrl("http://example.com/pfp.png");
    }

    @Test
    void testGetProfile() {
        when(userService.getProfile(1L)).thenReturn(mockResponse);

        ResponseEntity<UserResponseDTO> response = userController.getProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).getProfile(1L);
    }

    @Test
    void testUpdateProfile() {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        when(userService.updateProfile(1L, dto)).thenReturn(mockResponse);

        ResponseEntity<UserResponseDTO> response = userController.updateProfile(authentication, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).updateProfile(1L, dto);
    }

    @Test
    void testDeleteProfile() {
        doNothing().when(userService).deleteProfile(1L);

        ResponseEntity<String> response = userController.deleteProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Profile deleted successfully.", response.getBody());
        verify(userService, times(1)).deleteProfile(1L);
    }

    @Test
    void testUpdateProfilePicture() {
        when(userService.uploadPFP(1L, multipartFile)).thenReturn(mockResponse);

        ResponseEntity<UserResponseDTO> response = userController.updateProfilePicture(authentication, multipartFile);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).uploadPFP(1L, multipartFile);
    }

    @Test
    void testDeleteProfilePicture() {
        when(userService.deletePFP(1L)).thenReturn(mockResponse);

        ResponseEntity<UserResponseDTO> response = userController.deleteProfilePicture(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).deletePFP(1L);
    }
}
