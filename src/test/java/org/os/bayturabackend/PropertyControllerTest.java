package org.os.bayturabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.os.bayturabackend.DTOs.*;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.MediaService;
import org.os.bayturabackend.services.PropertyService;
import org.os.bayturabackend.controllers.PropertyController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private MediaService mediaService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PropertyController propertyController;

    private User mockUser;
    private PropertyResponseDTO mockProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User mockUser = mock(User.class);


        mockProperty = new PropertyResponseDTO();
        mockProperty.setId(100L);

        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void testGetProperties() {
        when(propertyService.getProperties(null, null, null, null, null, null, null))
                .thenReturn(Collections.singletonList(mockProperty));

        ResponseEntity<List<PropertyResponseDTO>> response = propertyController.getProperties(
                null, null, null, null, null, null, null
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(propertyService, times(1)).getProperties(
                null, null, null, null, null, null, null
        );
    }

    @Test
    void testGetPropertyById() {
        when(propertyService.getPropertyById(100L)).thenReturn(mockProperty);

        ResponseEntity<PropertyResponseDTO> response = propertyController.getPropertyById(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100L, response.getBody().getId());
        verify(propertyService, times(1)).getPropertyById(100L);
    }

    @Test
    void testGetMyProperties() {
        when(propertyService.getMyProperties(1L)).thenReturn(Collections.singletonList(mockProperty));

        ResponseEntity<List<PropertyResponseDTO>> response = propertyController.getMyProperties(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(propertyService, times(1)).getMyProperties(1L);
    }

    @Test
    void testCreateProperty() {
        PropertyRequestDTO dto = new PropertyRequestDTO();
        when(propertyService.createProperty(dto, 1L)).thenReturn(mockProperty);

        ResponseEntity<PropertyResponseDTO> response = propertyController.createProperty(authentication, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100L, response.getBody().getId());
        verify(propertyService, times(1)).createProperty(dto, 1L);
    }

    @Test
    void testDeletePropertyOwner() {
        doNothing().when(propertyService).deletePropertyOwner(100L, 1L);

        ResponseEntity<String> response = propertyController.deletePropertyOwner(authentication, 100L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Property deleted successfully.", response.getBody());
        verify(propertyService, times(1)).deletePropertyOwner(100L, 1L);
    }

    @Test
    void testUploadMedia() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(propertyService.getPropertyById(100L)).thenReturn(mockProperty);

        ResponseEntity<PropertyResponseDTO> response = propertyController.uploadMedia(authentication, 100L, mockFile);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100L, response.getBody().getId());
        verify(mediaService, times(1)).addMedia(100L, mockFile, 1L);
        verify(propertyService, times(1)).getPropertyById(100L);
    }
}
