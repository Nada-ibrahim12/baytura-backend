package org.os.bayturabackend.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestResponseDTO extends PropertyDetailsResponseDTO {
    private String status;
    private String customerName;
}
