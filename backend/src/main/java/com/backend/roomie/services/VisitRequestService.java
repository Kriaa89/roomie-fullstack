package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Notification;
import com.backend.roomie.models.VisitRequest;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.repositories.NotificationRepository;
import com.backend.roomie.repositories.VisitRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitRequestService {

    private final VisitRequestRepository visitRequestRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public VisitRequest createVisitRequest(Long senderId, Long receiverId, LocalDateTime requestedDateTime, String message) {
        // Validate users exist
        AppUser sender = appUserRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender user not found"));

        AppUser receiver = appUserRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver user not found"));

        // Create visit request
        VisitRequest visitRequest = VisitRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status(VisitRequest.VisitStatus.PENDING)
                .requestedDateTime(requestedDateTime)
                .message(message)
                .build();

        VisitRequest savedRequest = visitRequestRepository.save(visitRequest);

        // Create notification for receiver
        createVisitRequestNotification(receiverId, senderId, requestedDateTime);

        return savedRequest;
    }

    private void createVisitRequestNotification(Long recipientId, Long requesterId, LocalDateTime dateTime) {
        AppUser requester = appUserRepository.findById(requesterId).get();

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(requester.getFirstName() + " has requested a visit on " + 
                        dateTime.toLocalDate() + " at " + dateTime.toLocalTime())
                .type(Notification.NotificationType.VISIT_REQUEST)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public Optional<VisitRequest> getVisitRequestById(Long id) {
        return visitRequestRepository.findById(id);
    }

    public List<VisitRequest> getVisitRequestsBySender(Long senderId) {
        return visitRequestRepository.findBySenderId(senderId);
    }

    public List<VisitRequest> getVisitRequestsByReceiver(Long receiverId) {
        return visitRequestRepository.findByReceiverId(receiverId);
    }

    public List<VisitRequest> getPendingVisitRequestsByReceiver(Long receiverId) {
        return visitRequestRepository.findByReceiverIdAndStatus(receiverId, VisitRequest.VisitStatus.PENDING);
    }

    public List<VisitRequest> getUpcomingVisitRequests(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<VisitRequest> senderRequests = visitRequestRepository.findBySenderIdAndStatus(userId, VisitRequest.VisitStatus.CONFIRMED);
        List<VisitRequest> receiverRequests = visitRequestRepository.findByReceiverIdAndStatus(userId, VisitRequest.VisitStatus.CONFIRMED);

        // Filter for upcoming visits
        senderRequests.removeIf(request -> request.getRequestedDateTime().isBefore(now));
        receiverRequests.removeIf(request -> request.getRequestedDateTime().isBefore(now));

        // Combine lists
        senderRequests.addAll(receiverRequests);
        return senderRequests;
    }

    @Transactional
    public VisitRequest updateVisitRequestStatus(Long requestId, VisitRequest.VisitStatus newStatus) {
        VisitRequest visitRequest = visitRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Visit request not found"));

        visitRequest.setStatus(newStatus);
        VisitRequest updatedRequest = visitRequestRepository.save(visitRequest);

        // Create notification for sender about status change
        createVisitStatusChangeNotification(visitRequest.getSenderId(), visitRequest.getReceiverId(), newStatus);

        return updatedRequest;
    }

    private void createVisitStatusChangeNotification(Long recipientId, Long responderId, VisitRequest.VisitStatus status) {
        AppUser responder = appUserRepository.findById(responderId).get();

        String statusMessage;
        switch (status) {
            case CONFIRMED:
                statusMessage = "confirmed";
                break;
            case DECLINED:
                statusMessage = "declined";
                break;
            case RESCHEDULED:
                statusMessage = "requested to reschedule";
                break;
            default:
                return; // Don't notify for PENDING status
        }

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(responder.getFirstName() + " has " + statusMessage + " your visit request")
                .type(Notification.NotificationType.VISIT_REQUEST)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public VisitRequest rescheduleVisitRequest(Long requestId, LocalDateTime newDateTime) {
        VisitRequest visitRequest = visitRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Visit request not found"));

        visitRequest.setRequestedDateTime(newDateTime);
        visitRequest.setStatus(VisitRequest.VisitStatus.RESCHEDULED);

        return visitRequestRepository.save(visitRequest);
    }

    public void deleteVisitRequest(Long id) {
        visitRequestRepository.deleteById(id);
    }

    @Transactional
    public VisitRequest createVisitRequest(VisitRequest visitRequest) {
        return createVisitRequest(
            visitRequest.getSenderId(),
            visitRequest.getReceiverId(),
            visitRequest.getRequestedDateTime(),
            visitRequest.getMessage()
        );
    }

    public VisitRequest updateVisitRequest(VisitRequest visitRequest) {
        if (!visitRequestRepository.existsById(visitRequest.getId())) {
            throw new IllegalArgumentException("Visit request not found");
        }
        return visitRequestRepository.save(visitRequest);
    }

    public List<VisitRequest> getVisitRequestsBySenderId(Long senderId) {
        return getVisitRequestsBySender(senderId);
    }

    public List<VisitRequest> getVisitRequestsByReceiverId(Long receiverId) {
        return getVisitRequestsByReceiver(receiverId);
    }
}
