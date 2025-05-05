package com.backend.roomie.repositories;

import com.backend.roomie.models.VisitRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRequestRepository extends JpaRepository<VisitRequest, Long> {
    List<VisitRequest> findBySenderId(Long senderId);
    List<VisitRequest> findByReceiverId(Long receiverId);
    List<VisitRequest> findByStatus(VisitRequest.VisitStatus status);
    List<VisitRequest> findBySenderIdAndStatus(Long senderId, VisitRequest.VisitStatus status);
    List<VisitRequest> findByReceiverIdAndStatus(Long receiverId, VisitRequest.VisitStatus status);
    List<VisitRequest> findByRequestedDateTimeAfter(LocalDateTime dateTime);
    List<VisitRequest> findByReceiverIdAndRequestedDateTimeBetween(Long receiverId, LocalDateTime start, LocalDateTime end);
}