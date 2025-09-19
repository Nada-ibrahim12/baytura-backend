package org.os.bayturabackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePropertiesDTO {
    private Long favoriteId;
    private PropertyResponseDTO property;
}

