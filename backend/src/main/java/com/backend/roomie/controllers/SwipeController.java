package com.backend.roomie.controllers;

import com.backend.roomie.dtos.SwipeLikeDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RenterProfile;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.models.SwipeLike;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.RenterProfileService;
import com.backend.roomie.services.RoommateHostProfileService;
import com.backend.roomie.services.SwipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeLikeService swipeLikeService;
    private final AppUserService appUserService;
    private final RenterProfileService renterProfileService;
    private final RoommateHostProfileService roommateHostProfileService;

    @PostMapping("/like/{swipedId}")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<SwipeLikeDto> createLike(
            @PathVariable Long swipedId,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        SwipeLike swipe = swipeLikeService.createSwipe(currentUser.getId(), swipedId, SwipeLike.SwipeStatus.LIKE);
        
        return ResponseEntity.ok(mapToDto(swipe));
    }

    @PostMapping("/dislike/{swipedId}")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<SwipeLikeDto> createDislike(
            @PathVariable Long swipedId,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        SwipeLike swipe = swipeLikeService.createSwipe(currentUser.getId(), swipedId, SwipeLike.SwipeStatus.DISLIKE);
        
        return ResponseEntity.ok(mapToDto(swipe));
    }

    @PostMapping("/accept/{swiperId}")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<SwipeLikeDto> acceptSwipe(
            @PathVariable Long swiperId,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        
        // Check if the swiper has liked the current user
        SwipeLike existingSwipe = swipeLikeService.getSwipeBetweenUsers(swiperId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("No swipe found"));
        
        if (existingSwipe.getStatus() != SwipeLike.SwipeStatus.LIKE) {
            return ResponseEntity.badRequest().build();
        }
        
        // Create a like from current user to swiper, which will trigger match logic
        SwipeLike swipe = swipeLikeService.createSwipe(currentUser.getId(), swiperId, SwipeLike.SwipeStatus.LIKE);
        
        return ResponseEntity.ok(mapToDto(swipe));
    }

    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<List<SwipeLikeDto>> getReceivedSwipes(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<SwipeLike> swipes = swipeLikeService.getSwipesBySwipedId(currentUser.getId());
        
        List<SwipeLikeDto> swipeDtos = swipes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(swipeDtos);
    }

    @GetMapping("/sent")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<List<SwipeLikeDto>> getSentSwipes(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<SwipeLike> swipes = swipeLikeService.getSwipesBySwiperId(currentUser.getId());
        
        List<SwipeLikeDto> swipeDtos = swipes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(swipeDtos);
    }

    @GetMapping("/matches")
    @PreAuthorize("hasAnyRole('RENTER', 'ROOMMATE_HOST')")
    public ResponseEntity<List<SwipeLikeDto>> getMatches(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        List<SwipeLike> matches = swipeLikeService.getMatchesForUser(currentUser.getId());
        
        List<SwipeLikeDto> matchDtos = matches.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(matchDtos);
    }

    private SwipeLikeDto mapToDto(SwipeLike swipe) {
        AppUser swiper = appUserService.getUserById(swipe.getSwiperId())
                .orElseThrow(() -> new RuntimeException("Swiper not found"));
        
        AppUser swiped = appUserService.getUserById(swipe.getSwipedId())
                .orElseThrow(() -> new RuntimeException("Swiped user not found"));
        
        String swiperName = swiper.getFirstName() + " " + swiper.getLastName();
        String swipedName = swiped.getFirstName() + " " + swiped.getLastName();
        
        String swiperProfilePicture = null;
        String swipedProfilePicture = null;
        
        // Get profile pictures based on role
        if (swiper.getRole() == com.backend.roomie.models.Role.RENTER) {
            RenterProfile profile = renterProfileService.getRenterProfileByUserId(swiper.getId()).orElse(null);
            if (profile != null) {
                swiperProfilePicture = profile.getProfilePictureUrl();
            }
        } else if (swiper.getRole() == com.backend.roomie.models.Role.ROOMMATE_HOST) {
            RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileByUserId(swiper.getId()).orElse(null);
            if (profile != null) {
                swiperProfilePicture = profile.getProfilePictureUrl();
            }
        }
        
        if (swiped.getRole() == com.backend.roomie.models.Role.RENTER) {
            RenterProfile profile = renterProfileService.getRenterProfileByUserId(swiped.getId()).orElse(null);
            if (profile != null) {
                swipedProfilePicture = profile.getProfilePictureUrl();
            }
        } else if (swiped.getRole() == com.backend.roomie.models.Role.ROOMMATE_HOST) {
            RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileByUserId(swiped.getId()).orElse(null);
            if (profile != null) {
                swipedProfilePicture = profile.getProfilePictureUrl();
            }
        }
        
        return SwipeLikeDto.builder()
                .id(swipe.getId())
                .swiperId(swipe.getSwiperId())
                .swipedId(swipe.getSwipedId())
                .status(swipe.getStatus())
                .timestamp(swipe.getTimestamp())
                .swiperName(swiperName)
                .swiperProfilePicture(swiperProfilePicture)
                .swipedName(swipedName)
                .swipedProfilePicture(swipedProfilePicture)
                .build();
    }
}