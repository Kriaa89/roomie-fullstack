package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.Matches;
import com.backend.roomie.models.User;
import com.backend.roomie.services.MatchesService;
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
@RequestMapping("/api/matches")
public class MatchesController {

    private final MatchesService matchesService;
    private final UserService userService;

    @Autowired
    public MatchesController(MatchesService matchesService, UserService userService) {
        this.matchesService = matchesService;
        this.userService = userService;
    }

    /**
     * Get all matches for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllMatchesForUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);

            User user = new User();
            user.setId(userDTO.getId());

            List<Matches> matches = matchesService.getAllMatchesForUser(user);
            return new ResponseEntity<>(matches, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all active matches for a user
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveMatchesForUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);

            User user = new User();
            user.setId(userDTO.getId());

            List<Matches> matches = matchesService.getActiveMatchesForUser(user);
            return new ResponseEntity<>(matches, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Check if two users have a match
     */
    @GetMapping("/between")
    public ResponseEntity<?> getMatchBetweenUsers(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        try {
            UserDTO user1DTO = userService.getUserById(user1Id);
            UserDTO user2DTO = userService.getUserById(user2Id);

            User user1 = new User();
            user1.setId(user1DTO.getId());

            User user2 = new User();
            user2.setId(user2DTO.getId());

            Optional<Matches> matchOpt = matchesService.getMatchBetweenUsers(user1, user2);

            if (matchOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No match found between these users");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(matchOpt.get(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a match between two users
     */
    @PostMapping
    public ResponseEntity<?> createMatch(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        try {
            UserDTO user1DTO = userService.getUserById(user1Id);
            UserDTO user2DTO = userService.getUserById(user2Id);

            User user1 = new User();
            user1.setId(user1DTO.getId());

            User user2 = new User();
            user2.setId(user2DTO.getId());

            Matches match = matchesService.createMatch(user1, user2);
            return new ResponseEntity<>(match, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update the status of a match
     */
    @PutMapping("/{matchId}/status")
    public ResponseEntity<?> updateMatchStatus(
            @PathVariable Long matchId,
            @RequestParam String status) {

        Matches.MatchStatus matchStatus;
        try {
            matchStatus = Matches.MatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid match status. Must be ACTIVE, INACTIVE, or EXPIRED");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Matches> matchOpt = matchesService.updateMatchStatus(matchId, matchStatus);

        if (matchOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Match not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(matchOpt.get(), HttpStatus.OK);
    }

    /**
     * Unmatch two users
     */
    @PutMapping("/unmatch")
    public ResponseEntity<?> unmatchUsers(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        try {
            UserDTO user1DTO = userService.getUserById(user1Id);
            UserDTO user2DTO = userService.getUserById(user2Id);

            User user1 = new User();
            user1.setId(user1DTO.getId());

            User user2 = new User();
            user2.setId(user2DTO.getId());

            boolean unmatched = matchesService.unmatchUsers(user1, user2);

            if (!unmatched) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No match found between these users");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Users unmatched successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Mark notification as sent for a user in a match
     */
    @PutMapping("/{matchId}/notification")
    public ResponseEntity<?> markNotificationSent(
            @PathVariable Long matchId,
            @RequestParam Long userId) {

        try {
            UserDTO userDTO = userService.getUserById(userId);

            User user = new User();
            user.setId(userDTO.getId());

            Optional<Matches> matchOpt = matchesService.markNotificationSent(matchId, user);

            if (matchOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Match not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(matchOpt.get(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
