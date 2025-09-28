package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.*;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.AdminService;
import org.os.bayturabackend.services.RequestService;
import org.os.bayturabackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final RequestService requestService;
    private final UserService userService;

    @PutMapping("/provider/{id}/status")
    public ResponseEntity<String> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeStatusDTO body
        ) {
        adminService.changeStatus(
                id,
                body.getStatus()
        );
        return ResponseEntity.ok("Provider status updated");
    }
    @GetMapping("/customer-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequests(Authentication auth) {
        return ResponseEntity.ok(requestService.getRequests());
    }

    @GetMapping("/provider-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProviderResponseDTO> getAllProviderRequests(Authentication auth) {
        return userService.getProviders();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyName
    ) {
        return userService.getAllUsers(role, status, companyName);
    }


}
