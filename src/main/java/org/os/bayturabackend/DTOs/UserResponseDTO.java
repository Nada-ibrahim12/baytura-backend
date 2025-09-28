package org.os.bayturabackend.DTOs;

import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserResponseDTO extends BaseUserDTO {
    private Long id;
    private String profilePictureUrl;
    private String role;
}

