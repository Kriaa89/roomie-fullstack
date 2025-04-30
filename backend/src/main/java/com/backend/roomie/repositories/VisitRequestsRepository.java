package com.backend.roomie.repositories;

import com.backend.roomie.models.VisitRequests;
import com.backend.roomie.models.User;
import com.backend.roomie.models.PropertyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for VisitRequests entity
 */
@Repository
public interface VisitRequestsRepository extends JpaRepository<VisitRequests, Long> {
    List<VisitRequests> findByUser(User user);
    List<VisitRequests> findByPropertyList(PropertyList propertyList);
    List<VisitRequests> findByStatus(VisitRequests.Status status);
}
