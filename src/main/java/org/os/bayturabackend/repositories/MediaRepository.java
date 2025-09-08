package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByPropertyDetails_Id(Long propertyId);

}
