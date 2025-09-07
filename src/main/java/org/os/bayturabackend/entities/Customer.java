package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Customer extends User {

    @OneToMany(mappedBy = "customer")
    private Set<Favorite> favorites;

    @OneToMany(mappedBy = "customer")
    private Set<Request> requests;

    @OneToMany(mappedBy = "customer")
    private Set<SearchHistory> searchHistories;

    @OneToMany(mappedBy = "owner")
    private List<Property> properties;

    @Override
    public void setRole() {
        setRole(Role.CUSTOMER);
    }

    @Override
    public String getUserType() {
        return getRole().name();
    }
}