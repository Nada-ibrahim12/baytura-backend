package org.os.bayturabackend.repositories;

import org.os.bayturabackend.entities.Request;
import org.os.bayturabackend.entities.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByCustomer_UserId(Long customerId);
    List<Request> findByStatus(RequestStatus status);
    List<Request> findByCustomer_Username(String username);
    List<Request> findByStatusAndCustomer_Username(RequestStatus status, String username);
}
