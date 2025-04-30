package com.backend.roomie.services;

import com.backend.roomie.models.Matches;
import com.backend.roomie.models.User;
import com.backend.roomie.repositories.MatchesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchesService {

    private final MatchesRepository matchesRepository;

    @Autowired
    public MatchesService(MatchesRepository matchesRepository) {
        this.matchesRepository = matchesRepository;
    }

    /**
     * Get all matches for a user (either as user1 or user2)
     * @param user The user
     * @return List of matches
     */
    public List<Matches> getAllMatchesForUser(User user) {
        return matchesRepository.findAllMatchesForUser(user);
    }

    /**
     * Get all active matches for a user
     * @param user The user
     * @return List of active matches
     */
    public List<Matches> getActiveMatchesForUser(User user) {
        return matchesRepository.findActiveMatchesForUser(user);
    }

    /**
     * Check if two users have a match
     * @param user1 First user
     * @param user2 Second user
     * @return Optional containing the match if it exists
     */
    public Optional<Matches> getMatchBetweenUsers(User user1, User user2) {
        return matchesRepository.findMatchBetweenUsers(user1, user2);
    }

    /**
     * Create a new match between two users
     * @param user1 First user
     * @param user2 Second user
     * @return The created match
     */
    public Matches createMatch(User user1, User user2) {
        // Check if a match already exists
        Optional<Matches> existingMatch = matchesRepository.findMatchBetweenUsers(user1, user2);
        
        if (existingMatch.isPresent()) {
            Matches match = existingMatch.get();
            // If the match exists but is inactive, reactivate it
            if (match.getStatus() != Matches.MatchStatus.ACTIVE) {
                match.setStatus(Matches.MatchStatus.ACTIVE);
                return matchesRepository.save(match);
            }
            return match;
        }
        
        // Create a new match
        Matches match = new Matches();
        match.setUser1(user1);
        match.setUser2(user2);
        match.setStatus(Matches.MatchStatus.ACTIVE);
        return matchesRepository.save(match);
    }

    /**
     * Update the status of a match
     * @param matchId The match ID
     * @param status The new status
     * @return The updated match
     */
    public Optional<Matches> updateMatchStatus(Long matchId, Matches.MatchStatus status) {
        Optional<Matches> matchOptional = matchesRepository.findById(matchId);
        
        if (matchOptional.isPresent()) {
            Matches match = matchOptional.get();
            match.setStatus(status);
            return Optional.of(matchesRepository.save(match));
        }
        
        return Optional.empty();
    }

    /**
     * Unmatch two users
     * @param user1 First user
     * @param user2 Second user
     * @return true if unmatched successfully, false otherwise
     */
    public boolean unmatchUsers(User user1, User user2) {
        Optional<Matches> matchOptional = matchesRepository.findMatchBetweenUsers(user1, user2);
        
        if (matchOptional.isPresent()) {
            Matches match = matchOptional.get();
            match.setStatus(Matches.MatchStatus.INACTIVE);
            matchesRepository.save(match);
            return true;
        }
        
        return false;
    }

    /**
     * Mark notification as sent for a user in a match
     * @param matchId The match ID
     * @param user The user
     * @return The updated match
     */
    public Optional<Matches> markNotificationSent(Long matchId, User user) {
        Optional<Matches> matchOptional = matchesRepository.findById(matchId);
        
        if (matchOptional.isPresent()) {
            Matches match = matchOptional.get();
            
            if (match.getUser1().getId().equals(user.getId())) {
                match.setUser1Notified(true);
            } else if (match.getUser2().getId().equals(user.getId())) {
                match.setUser2Notified(true);
            }
            
            return Optional.of(matchesRepository.save(match));
        }
        
        return Optional.empty();
    }
}