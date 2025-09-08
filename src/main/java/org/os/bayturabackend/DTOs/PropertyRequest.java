package org.os.bayturabackend.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.os.bayturabackend.entities.PropertyType;

import java.time.LocalDateTime;

@Data
public class PropertyRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotBlank(message = "Type is mandatory")
    private PropertyType type;

    @NotBlank(message = "Price is mandatory")
    private double price;

    @NotBlank(message = "Area is mandatory")
    private Double area;

    @NotBlank(message = "Address is mandatory")
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId;
}
