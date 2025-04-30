package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.PropertyList;
import com.backend.roomie.models.Swipe;
import com.backend.roomie.models.User;
import com.backend.roomie.services.PropertyService;
import com.backend.roomie.services.SwipeService;
import com.backend.roomie.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/swipes")
public class SwipeController {

    private final SwipeService swipeService;
    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public SwipeController(SwipeService swipeService, UserService userService, PropertyService propertyService) {
        this.swipeService = swipeService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    /**
     * Create a new user-to-user swipe
     */
    @PostMapping("/user")
    public ResponseEntity<?> createUserSwipe(
            @RequestParam Long swiperId,
            @RequestParam Long targetUserId,
            @RequestParam String direction) {

        try {
            UserDTO swiperDTO = userService.getUserById(swiperId);
            UserDTO targetUserDTO = userService.getUserById(targetUserId);

            User swiper = new User();
            swiper.setId(swiperDTO.getId());

            User targetUser = new User();
            targetUser.setId(targetUserDTO.getId());

            Swipe.Status status;
            try {
                status = Swipe.Status.valueOf(direction.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid swipe direction. Must be LEFT or RIGHT");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Swipe swipe = swipeService.createUserSwipe(swiper, targetUser, status);
            return new ResponseEntity<>(swipe, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new user-to-property swipe
     */
    @PostMapping("/property")
    public ResponseEntity<?> createPropertySwipe(
            @RequestParam Long swiperId,
            @RequestParam Long propertyId,
            @RequestParam String direction) {

        try {
            UserDTO swiperDTO = userService.getUserById(swiperId);
            PropertyList property = propertyService.getPropertyById(propertyId);

            User swiper = new User();
            swiper.setId(swiperDTO.getId());

            Swipe.Status status;
            try {
                status = Swipe.Status.valueOf(direction.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid swipe direction. Must be LEFT or RIGHT");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Swipe swipe = swipeService.createPropertySwipe(swiper, property, status);
            return new ResponseEntity<>(swipe, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User or property not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all swipes by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSwipesByUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);

            User user = new User();
            user.setId(userDTO.getId());

            List<Swipe> swipes = swipeService.getSwipesByUser(user);
            return new ResponseEntity<>(swipes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all right swipes by a user
     */
    @GetMapping("/user/{userId}/right")
    public ResponseEntity<?> getRightSwipesByUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);

            User user = new User();
            user.setId(userDTO.getId());

            List<Swipe> swipes = swipeService.getRightSwipesByUser(user);
            return new ResponseEntity<>(swipes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all swipes on a property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<?> getSwipesOnProperty(@PathVariable Long propertyId) {
        try {
            PropertyList property = propertyService.getPropertyById(propertyId);

            List<Swipe> swipes = swipeService.getSwipesOnProperty(property);
            return new ResponseEntity<>(swipes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Property not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all right swipes on a property
     */
    @GetMapping("/property/{propertyId}/right")
    public ResponseEntity<?> getRightSwipesOnProperty(@PathVariable Long propertyId) {
        try {
            PropertyList property = propertyService.getPropertyById(propertyId);

            List<Swipe> swipes = swipeService.getRightSwipesOnProperty(property);
            return new ResponseEntity<>(swipes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Property not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
