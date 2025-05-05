package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Notification;
import com.backend.roomie.models.SwipeLike;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.repositories.NotificationRepository;
import com.backend.roomie.repositories.SwipeLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SwipeLikeService {

    private final SwipeLikeRepository swipeLikeRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public SwipeLike createSwipe(Long swiperId, Long swipedId, SwipeLike.SwipeStatus status) {
        // Validate users exist
        AppUser swiper = appUserRepository.findById(swiperId)
                .orElseThrow(() -> new IllegalArgumentException("Swiper user not found"));
        
        AppUser swiped = appUserRepository.findById(swipedId)
                .orElseThrow(() -> new IllegalArgumentException("Swiped user not found"));
        
        // Check if swipe already exists
        Optional<SwipeLike> existingSwipe = swipeLikeRepository.findBySwiperIdAndSwipedId(swiperId, swipedId);
        if (existingSwipe.isPresent()) {
            SwipeLike swipe = existingSwipe.get();
            swipe.setStatus(status);
            swipe.setTimestamp(LocalDateTime.now());
            return swipeLikeRepository.save(swipe);
        }
        
        // Create new swipe
        SwipeLike swipe = SwipeLike.builder()
                .swiperId(swiperId)
                .swipedId(swipedId)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
        
        SwipeLike savedSwipe = swipeLikeRepository.save(swipe);
        
        // Check for match if this is a LIKE
        if (status == SwipeLike.SwipeStatus.LIKE) {
            checkForMatch(swiperId, swipedId);
        }
        
        return savedSwipe;
    }

    private void checkForMatch(Long swiperId, Long swipedId) {
        // Check if the other user has already liked this user
        Optional<SwipeLike> otherSwipe = swipeLikeRepository.findBySwiperIdAndSwipedId(swipedId, swiperId);
        
        if (otherSwipe.isPresent() && otherSwipe.get().getStatus() == SwipeLike.SwipeStatus.LIKE) {
            // It's a match! Update both swipes to ACCEPTED
            otherSwipe.get().setStatus(SwipeLike.SwipeStatus.ACCEPTED);
            swipeLikeRepository.save(otherSwipe.get());
            
            SwipeLike thisSwipe = swipeLikeRepository.findBySwiperIdAndSwipedId(swiperId, swipedId).get();
            thisSwipe.setStatus(SwipeLike.SwipeStatus.ACCEPTED);
            swipeLikeRepository.save(thisSwipe);
            
            // Create notifications for both users
            createMatchNotification(swiperId, swipedId);
            createMatchNotification(swipedId, swiperId);
        }
    }

    private void createMatchNotification(Long recipientId, Long matchedWithId) {
        AppUser matchedWith = appUserRepository.findById(matchedWithId).get();
        
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content("You have a new match with " + matchedWith.getFirstName() + "!")
                .type(Notification.NotificationType.MATCH)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
    }

    public List<SwipeLike> getSwipesBySwiperId(Long swiperId) {
        return swipeLikeRepository.findBySwiperId(swiperId);
    }

    public List<SwipeLike> getSwipesBySwipedId(Long swipedId) {
        return swipeLikeRepository.findBySwipedId(swipedId);
    }

    public List<SwipeLike> getMatchesForUser(Long userId) {
        return swipeLikeRepository.findBySwiperIdAndStatus(userId, SwipeLike.SwipeStatus.ACCEPTED);
    }

    public Optional<SwipeLike> getSwipeBetweenUsers(Long swiperId, Long swipedId) {
        return swipeLikeRepository.findBySwiperIdAndSwipedId(swiperId, swipedId);
    }

    public void deleteSwipe(Long swiperId, Long swipedId) {
        Optional<SwipeLike> swipe = swipeLikeRepository.findBySwiperIdAndSwipedId(swiperId, swipedId);
        swipe.ifPresent(s -> swipeLikeRepository.deleteById(s.getId()));
    }
}