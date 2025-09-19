package org.os.bayturabackend.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PropertyResponseDTO extends PropertyDetailsResponseDTO {
    private String propertyStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ownerName;
}

