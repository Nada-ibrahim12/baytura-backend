package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "providers")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends User{

    @Column(name = "company_name" , nullable = false)
    private String companyName;

    @Column(name = "company_address" , nullable = false)
    private String companyAddress;

    @Column(name = "is_approved" , nullable = false)
    private Boolean isApproved;

    @OneToMany(mappedBy = "owner")
    private List<Property> properties;


    @Override
    public void setRole() {
        setRole(Role.PROVIDER);
    }

    @Override
    public String getUserType() {
        return getRole().name();
    }
}
