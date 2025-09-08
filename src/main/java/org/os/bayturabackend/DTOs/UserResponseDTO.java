package org.os.bayturabackend.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserResponseDTO extends BaseUserDTO {
    private String profilePictureUrl;
    private String role;
}

