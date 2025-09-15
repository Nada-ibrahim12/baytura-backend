package org.os.bayturabackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.os.bayturabackend.entities.PropertyPurpose ;
import org.os.bayturabackend.entities.PropertyPurpose;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PropertyDetailsResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String purpose;
    private String description;
    private double price;
    private Double area;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<MediaResponse> images;
}
