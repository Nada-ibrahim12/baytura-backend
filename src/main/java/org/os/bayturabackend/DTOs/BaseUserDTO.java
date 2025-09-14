package org.os.bayturabackend.DTOs;

import lombok.*;

import lombok.Data;
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUserDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}
