package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Customer;
import org.os.bayturabackend.entities.Favorite;
import org.os.bayturabackend.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByCustomerAndProperty(Customer customer, Property property);
    List<Favorite> findByCustomer(Customer customer);


}
