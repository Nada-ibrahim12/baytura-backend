package org.os.bayturabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.os.bayturabackend.DTOs.ChangeStatusDTO;
import org.os.bayturabackend.DTOs.RequestCreateDTO;
import org.os.bayturabackend.DTOs.RequestResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.RequestService;
import org.os.bayturabackend.controllers.RequestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestControllerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private Authentication authentication;

    @Mock
    private User mockUser;

    @InjectMocks
    private RequestController requestController;

    private RequestResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock User
        when(mockUser.getUserId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        // mock Response DTO
        mockResponse = new RequestResponseDTO();
        mockResponse.setId(100L);
        mockResponse.setStatus("PENDING");
    }

    @Test
    void testGetRequestsByCustomer() {
        when(requestService.getRequestsByCustomer(1L)).thenReturn(Collections.singletonList(mockResponse));

        ResponseEntity<List<RequestResponseDTO>> response = requestController.getRequestsByCustomer(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getId());
        verify(requestService, times(1)).getRequestsByCustomer(1L);
    }

    @Test
    void testGetRequestByIdCustomer() {
        when(requestService.getRequestByIdCustomer(100L, 1L)).thenReturn(mockResponse);

        ResponseEntity<RequestResponseDTO> response = requestController.getRequestByIdCustomer(authentication, "100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
        verify(requestService, times(1)).getRequestByIdCustomer(100L, 1L);
    }

    @Test
    void testDeleteRequestByIdCustomer() {
        doNothing().when(requestService).deleteRequestByIdCustomer(100L, 1L);

        ResponseEntity<String> response = requestController.deleteRequestByIdCustomer(authentication, "100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Request deleted successfully.", response.getBody());
        verify(requestService, times(1)).deleteRequestByIdCustomer(100L, 1L);
    }

    @Test
    void testCreateRequest() {
        RequestCreateDTO dto = new RequestCreateDTO();
        when(requestService.createNewRequest(dto, 1L)).thenReturn(mockResponse);

        ResponseEntity<RequestResponseDTO> response = requestController.createRequest(authentication, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PENDING", response.getBody().getStatus());
        verify(requestService, times(1)).createNewRequest(dto, 1L);
    }

    @Test
    void testGetRequestByIdAdmin() {
        when(requestService.getRequestByIdAdmin(100L)).thenReturn(mockResponse);

        ResponseEntity<RequestResponseDTO> response = requestController.getRequestByIdAdmin("100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
        verify(requestService, times(1)).getRequestByIdAdmin(100L);
    }

    @Test
    void testChangeRequestStatus() {
        ChangeStatusDTO dto = new ChangeStatusDTO();
        dto.setStatus("APPROVED");
        when(requestService.changeRequestStatus(100L, "APPROVED")).thenReturn(mockResponse);

        ResponseEntity<RequestResponseDTO> response = requestController.changeRequestStatus("100", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(requestService, times(1)).changeRequestStatus(100L, "APPROVED");
    }

    @Test
    void testGetRequestsAdmin() {
        when(requestService.getRequestsAdmin("PENDING", "john")).thenReturn(Collections.singletonList(mockResponse));

        ResponseEntity<List<RequestResponseDTO>> response = requestController.getRequestsAdmin("PENDING", "john");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(requestService, times(1)).getRequestsAdmin("PENDING", "john");
    }

    @Test
    void testDeleteRequestByIdAdmin() {
        doNothing().when(requestService).deleteRequestByIdAdmin(100L);

        ResponseEntity<String> response = requestController.deleteRequestByIdAdmin(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Request deleted successfully.", response.getBody());
        verify(requestService, times(1)).deleteRequestByIdAdmin(100L);
    }
}
