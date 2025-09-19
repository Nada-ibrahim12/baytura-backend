package org.os.bayturabackend.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProviderDTO extends BaseUserDTO {
    private String companyName;
    private String companyAddress;
}
