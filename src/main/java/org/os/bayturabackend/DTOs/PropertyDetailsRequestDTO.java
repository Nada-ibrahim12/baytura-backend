package org.os.bayturabackend.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.os.bayturabackend.entities.PropertyPurpose ;
import org.os.bayturabackend.entities.PropertyPurpose;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PropertyDetailsRequestDTO {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Type is mandatory")
    private String type;

    @NotNull(message = "Purpose is mandatory")
    private PropertyPurpose purpose;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Area is mandatory")
    @Positive(message = "Area must be greater than 0")
    private Double area;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotNull(message = "Latitude is mandatory")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is mandatory")
    private BigDecimal longitude;
}
