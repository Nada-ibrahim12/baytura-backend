package org.os.bayturabackend.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "properties")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property extends PropertyDetails {
    @Enumerated(EnumType.STRING)
    @Column(name = "property_status",nullable = false)
    private PropertyStatus propertyStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // ? Owner: Customer or Provider
    private User owner;
}
