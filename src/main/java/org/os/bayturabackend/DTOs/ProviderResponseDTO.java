package org.os.bayturabackend.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProviderResponseDTO extends UserResponseDTO {
    private String companyName;
    private String companyAddress;
    private String status;
}
