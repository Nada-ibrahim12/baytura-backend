package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "requests")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request extends PropertyDetails {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;
}
