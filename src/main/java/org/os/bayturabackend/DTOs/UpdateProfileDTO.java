package org.os.bayturabackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String phone;

    // ? Provider-specific fields (optional)
    private String companyName;
    private String companyAddress;
}
