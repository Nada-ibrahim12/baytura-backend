package org.os.bayturabackend.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterUserDTO extends BaseUserDTO {  // ? for Customer and Admin
    private String password;
}

