package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}
