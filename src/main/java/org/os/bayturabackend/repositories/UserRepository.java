package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
