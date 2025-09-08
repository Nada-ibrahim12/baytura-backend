package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.entities.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAll();
    List<Property> findAllById(Iterable<Long> longs); // compare
    Optional<Property> findById(Long aLong);
    Property findByTitle(String title);
    Property findByAddress(String address);
    Property findByArea(Double area);
    Property save(Property property);
    void delete(Property entity);
    Property deleteById(long id);
    List<Property> findByPropertyStatus(PropertyStatus status);
}
