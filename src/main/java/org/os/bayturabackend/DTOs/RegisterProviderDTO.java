package org.os.bayturabackend.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterProviderDTO extends ProviderDTO {  // ? for provider only
    private String password;
}
