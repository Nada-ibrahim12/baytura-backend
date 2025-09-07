package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Override
    void delete(Media entity);

    @Override
    void deleteAll();

    @Override
    void deleteById(Long aLong);

    @Override
    Optional<Media> findById(Long aLong);

    @Override
    List<Media> findAllById(Iterable<Long> longs);

    @Override
    <S extends Media> S save(S entity);
}
