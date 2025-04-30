package com.backend.roomie.repositories;

import com.backend.roomie.models.Matches;
import com.backend.roomie.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchesRepository extends JpaRepository<Matches, Long> {
    
    // Find matches by user1
    List<Matches> findByUser1(User user1);
    
    // Find matches by user2
    List<Matches> findByUser2(User user2);
    
    // Find matches by status
    List<Matches> findByStatus(Matches.MatchStatus status);
    
    // Find matches by user1 and status
    List<Matches> findByUser1AndStatus(User user1, Matches.MatchStatus status);
    
    // Find matches by user2 and status
    List<Matches> findByUser2AndStatus(User user2, Matches.MatchStatus status);
    
    // Find match between two users (regardless of which is user1 or user2)
    @Query("SELECT m FROM Matches m WHERE (m.user1 = :user1 AND m.user2 = :user2) OR (m.user1 = :user2 AND m.user2 = :user1)")
    Optional<Matches> findMatchBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Find all matches for a user (either as user1 or user2)
    @Query("SELECT m FROM Matches m WHERE m.user1 = :user OR m.user2 = :user")
    List<Matches> findAllMatchesForUser(@Param("user") User user);
    
    // Find all active matches for a user
    @Query("SELECT m FROM Matches m WHERE (m.user1 = :user OR m.user2 = :user) AND m.status = 'ACTIVE'")
    List<Matches> findActiveMatchesForUser(@Param("user") User user);
}