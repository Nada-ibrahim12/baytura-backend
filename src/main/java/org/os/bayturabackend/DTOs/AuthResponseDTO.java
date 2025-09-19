package org.os.bayturabackend.DTOs;

import lombok.*;

import java.time.Instant;
@Builder

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private Instant expiresAt;
}
