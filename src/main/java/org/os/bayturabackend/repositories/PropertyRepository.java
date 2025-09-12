package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.entities.PropertyStatus;
import org.os.bayturabackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> , JpaSpecificationExecutor<Property> {
    List<Property> findByOwner_UserId(Long userId);
    Integer countByOwner(User userId);

}
