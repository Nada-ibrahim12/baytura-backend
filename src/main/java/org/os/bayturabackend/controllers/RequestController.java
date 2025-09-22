package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.os.bayturabackend.DTOs.ChangeStatusDTO;
import org.os.bayturabackend.DTOs.RequestCreateDTO;
import org.os.bayturabackend.DTOs.RequestResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.RequestService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

// ! ===================================================================================================================

// ? CUSTOMER
    @GetMapping("/requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByCustomer(Authentication auth) {
        User user = (User)auth.getPrincipal();
        Long customerId = user.getUserId();
        return ResponseEntity.ok(requestService.getRequestsByCustomer(customerId));
    }

    @GetMapping("/requests/{requestId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RequestResponseDTO> getRequestByIdCustomer(
            Authentication auth,
            @PathVariable String requestId
    ) {
        User user = (User)auth.getPrincipal();
        Long customerId = user.getUserId();
        return ResponseEntity.ok(requestService.getRequestByIdCustomer(Long.parseLong(requestId), customerId));
    }

    @DeleteMapping("/requests/{requestId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> deleteRequestByIdCustomer(
            Authentication auth,
            @PathVariable String requestId
    ){
        User user = (User)auth.getPrincipal();
        Long customerId = user.getUserId();
        requestService.deleteRequestByIdCustomer(Long.parseLong(requestId), customerId);
        return ResponseEntity.ok("Request deleted successfully.");
    }

    @PostMapping(value = "/requests", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RequestResponseDTO> createRequest(
            Authentication auth,
            @ModelAttribute RequestCreateDTO request
    ) throws IOException {
        User user = (User)auth.getPrincipal();
        Long customerId = user.getUserId();
        return ResponseEntity.ok(
                requestService.createNewRequest(request,customerId)
        );
    }


// ! ===================================================================================================================

// ? ADMIN

    @GetMapping("/admin/requests/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponseDTO> getRequestByIdAdmin(
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(requestService.getRequestByIdAdmin(Long.parseLong(requestId)));
    }

    @PutMapping("admin/requests/{requestId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponseDTO> changeRequestStatus(
            @PathVariable String requestId,
            @RequestBody ChangeStatusDTO body
    ) {
        return ResponseEntity.ok(
                requestService
                        .changeRequestStatus(
                                Long.parseLong(requestId),
                                body.getStatus()
                        )
        );
    }

    @GetMapping("/admin/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(requestService.getRequestsAdmin(status, username));
    }

    @DeleteMapping("/admin/requests/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRequestByIdAdmin(
            @PathVariable Long requestId
    ){
        requestService.deleteRequestByIdAdmin(requestId);
        return ResponseEntity.ok("Request deleted successfully.");
    }

}
