package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.AuthResponseDTO;
import org.os.bayturabackend.DTOs.LoginRequestDTO;
import org.os.bayturabackend.DTOs.RegisterProviderDTO;
import org.os.bayturabackend.DTOs.RegisterUserDTO;
import org.os.bayturabackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponseDTO> registerCustomer(@RequestBody RegisterUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCustomer(dto));
    }

    @PostMapping("/register/provider")
    public ResponseEntity<AuthResponseDTO> registerProvider(@RequestBody RegisterProviderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerProvider(dto));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponseDTO> registerAdmin(@RequestBody RegisterUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
