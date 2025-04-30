package com.backend.roomie.services;

import com.backend.roomie.models.Matches;
import com.backend.roomie.models.PropertyList;
import com.backend.roomie.models.Swipe;
import com.backend.roomie.models.User;
import com.backend.roomie.repositories.MatchesRepository;
import com.backend.roomie.repositories.SwipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final MatchesRepository matchesRepository;

    @Autowired
    public SwipeService(SwipeRepository swipeRepository, MatchesRepository matchesRepository) {
        this.swipeRepository = swipeRepository;
        this.matchesRepository = matchesRepository;
    }

    /**
     * Create a new swipe from a user to another user
     * @param swiper The user who is swiping
     * @param targetUser The user being swiped on
     * @param status The swipe status (LEFT or RIGHT)
     * @return The created swipe
     */
    public Swipe createUserSwipe(User swiper, User targetUser, Swipe.Status status) {
        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setTargetUser(targetUser);
        swipe.setStatus(status);

        Swipe savedSwipe = swipeRepository.save(swipe);

        // If this is a RIGHT swipe, check for a match
        if (status == Swipe.Status.RIGHT) {
            checkForUserMatch(swiper, targetUser);
        }

        return savedSwipe;
    }

    /**
     * Create a new swipe from a user to a property
     * @param swiper The user who is swiping
     * @param property The property being swiped on
     * @param status The swipe status (LEFT or RIGHT)
     * @return The created swipe
     */
    public Swipe createPropertySwipe(User swiper, PropertyList property, Swipe.Status status) {
        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setPropertyList(property);
        swipe.setStatus(status);

        return swipeRepository.save(swipe);
    }

    /**
     * Check if there's a match between two users
     * @param user1 First user
     * @param user2 Second user
     * @return true if a match was created, false otherwise
     */
    private boolean checkForUserMatch(User user1, User user2) {
        // Check if user2 has already swiped right on user1
        List<Swipe> reciprocalSwipes = swipeRepository.findByTargetUserAndStatus(user1, Swipe.Status.RIGHT);
        boolean hasMatch = reciprocalSwipes.stream()
                .anyMatch(swipe -> swipe.getSwiper().getId().equals(user2.getId()));

        if (hasMatch) {
            // Check if a match already exists
            Optional<Matches> existingMatch = matchesRepository.findMatchBetweenUsers(user1, user2);

            if (existingMatch.isEmpty()) {
                // Create a new match
                Matches match = new Matches();
                match.setUser1(user1);
                match.setUser2(user2);
                match.setStatus(Matches.MatchStatus.ACTIVE);
                matchesRepository.save(match);
                return true;
            }
        }

        return false;
    }

    /**
     * Get all swipes by a user
     * @param user The user
     * @return List of swipes
     */
    public List<Swipe> getSwipesByUser(User user) {
        return swipeRepository.findBySwiper(user);
    }

    /**
     * Get all right swipes by a user
     * @param user The user
     * @return List of right swipes
     */
    public List<Swipe> getRightSwipesByUser(User user) {
        return swipeRepository.findBySwiperAndStatus(user, Swipe.Status.RIGHT);
    }

    /**
     * Get all left swipes by a user
     * @param user The user
     * @return List of left swipes
     */
    public List<Swipe> getLeftSwipesByUser(User user) {
        return swipeRepository.findBySwiperAndStatus(user, Swipe.Status.LEFT);
    }

    /**
     * Get all swipes on a property
     * @param property The property
     * @return List of swipes
     */
    public List<Swipe> getSwipesOnProperty(PropertyList property) {
        return swipeRepository.findByPropertyList(property);
    }

    /**
     * Get all right swipes on a property
     * @param property The property
     * @return List of right swipes
     */
    public List<Swipe> getRightSwipesOnProperty(PropertyList property) {
        return swipeRepository.findByPropertyListAndStatus(property, Swipe.Status.RIGHT);
    }
}
