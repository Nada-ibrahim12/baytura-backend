package org.os.bayturabackend.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_details")
public class PropertyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String description;

    @NotNull
    private Double size;

    private Integer bedrooms;
    private Integer bathrooms;

    private Boolean hasGarage;
    private Boolean hasGarden;
    private Boolean hasSwimmingPool;

    @OneToOne
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    @OneToMany(mappedBy = "propertyDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();
}
