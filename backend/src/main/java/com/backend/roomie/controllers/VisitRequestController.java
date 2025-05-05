package com.backend.roomie.controllers;

import com.backend.roomie.dtos.VisitRequestDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.VisitRequest;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.VisitRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visit-requests")
@RequiredArgsConstructor
public class VisitRequestController {

    private final VisitRequestService visitRequestService;
    private final AppUserService appUserService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<VisitRequestDto> createVisitRequest(
            @RequestBody VisitRequestDto requestDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        
        VisitRequest visitRequest = VisitRequest.builder()
                .senderId(currentUser.getId())
                .receiverId(requestDto.getReceiverId())
                .status(VisitRequest.VisitStatus.PENDING)
                .requestedDateTime(requestDto.getRequestedDateTime())
                .message(requestDto.getMessage())
                .build();
        
        VisitRequest savedRequest = visitRequestService.createVisitRequest(visitRequest);
        return ResponseEntity.ok(mapToDto(savedRequest));
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<VisitRequestDto> confirmVisitRequest(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        VisitRequest visitRequest = visitRequestService.getVisitRequestById(id)
                .orElseThrow(() -> new RuntimeException("Visit request not found"));
        
        // Verify the current user is the receiver
        if (!visitRequest.getReceiverId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        visitRequest.setStatus(VisitRequest.VisitStatus.CONFIRMED);
        VisitRequest updatedRequest = visitRequestService.updateVisitRequest(visitRequest);
        return ResponseEntity.ok(mapToDto(updatedRequest));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<VisitRequestDto> rescheduleVisitRequest(
            @PathVariable Long id,
            @RequestBody VisitRequestDto requestDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        VisitRequest visitRequest = visitRequestService.getVisitRequestById(id)
                .orElseThrow(() -> new RuntimeException("Visit request not found"));
        
        // Verify the current user is the receiver
        if (!visitRequest.getReceiverId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        visitRequest.setStatus(VisitRequest.VisitStatus.RESCHEDULED);
        visitRequest.setRequestedDateTime(requestDto.getRequestedDateTime());
        visitRequest.setMessage(requestDto.getMessage());
        
        VisitRequest updatedRequest = visitRequestService.updateVisitRequest(visitRequest);
        return ResponseEntity.ok(mapToDto(updatedRequest));
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<VisitRequestDto> declineVisitRequest(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        VisitRequest visitRequest = visitRequestService.getVisitRequestById(id)
                .orElseThrow(() -> new RuntimeException("Visit request not found"));
        
        // Verify the current user is the receiver
        if (!visitRequest.getReceiverId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        visitRequest.setStatus(VisitRequest.VisitStatus.DECLINED);
        VisitRequest updatedRequest = visitRequestService.updateVisitRequest(visitRequest);
        return ResponseEntity.ok(mapToDto(updatedRequest));
    }

    @GetMapping("/sent")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<List<VisitRequestDto>> getSentVisitRequests(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<VisitRequest> requests = visitRequestService.getVisitRequestsBySenderId(currentUser.getId());
        
        List<VisitRequestDto> requestDtos = requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(requestDtos);
    }

    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<List<VisitRequestDto>> getReceivedVisitRequests(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<VisitRequest> requests = visitRequestService.getVisitRequestsByReceiverId(currentUser.getId());
        
        List<VisitRequestDto> requestDtos = requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(requestDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST', 'OWNER')")
    public ResponseEntity<VisitRequestDto> getVisitRequestById(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        VisitRequest visitRequest = visitRequestService.getVisitRequestById(id)
                .orElseThrow(() -> new RuntimeException("Visit request not found"));
        
        // Verify the current user is either the sender or receiver
        if (!visitRequest.getSenderId().equals(currentUser.getId()) && 
            !visitRequest.getReceiverId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(mapToDto(visitRequest));
    }

    private VisitRequestDto mapToDto(VisitRequest visitRequest) {
        AppUser sender = appUserService.getUserById(visitRequest.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        AppUser receiver = appUserService.getUserById(visitRequest.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        String senderName = sender.getFirstName() + " " + sender.getLastName();
        String receiverName = receiver.getFirstName() + " " + receiver.getLastName();
        
        // Note: In a real application, you would also fetch profile pictures and property details
        
        return VisitRequestDto.builder()
                .id(visitRequest.getId())
                .senderId(visitRequest.getSenderId())
                .receiverId(visitRequest.getReceiverId())
                .status(visitRequest.getStatus())
                .requestedDateTime(visitRequest.getRequestedDateTime())
                .message(visitRequest.getMessage())
                .senderName(senderName)
                .receiverName(receiverName)
                .build();
    }
}