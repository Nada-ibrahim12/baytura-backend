package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
//@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends User {

    @Override
    public void setRole() {
        setRole(Role.ADMIN);
    }

    public String getUserType() {
        return getRole().name();
    }
}