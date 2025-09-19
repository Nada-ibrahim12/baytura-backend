package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String url;

    @NotBlank
    @Column(nullable = false)
    private String altName;

    @ManyToOne
    @JoinColumn(name = "property_details_id", nullable = false)
    private PropertyDetails propertyDetails;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId;
}
