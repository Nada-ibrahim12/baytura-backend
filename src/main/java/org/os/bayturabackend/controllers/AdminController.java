package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/provider/{id}/approve")
    public ResponseEntity<String> approveProvider(@PathVariable Long id) {
        adminService.approveProvider(id);
        return ResponseEntity.ok("Provider approved");
    }
}
