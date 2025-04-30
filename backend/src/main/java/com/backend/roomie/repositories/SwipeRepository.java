package com.backend.roomie.repositories;

import com.backend.roomie.models.Swipe;
import com.backend.roomie.models.User;
import com.backend.roomie.models.PropertyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    // Find swipes by swiper
    List<Swipe> findBySwiper(User swiper);

    // Find swipes by target user
    List<Swipe> findByTargetUser(User targetUser);

    // Find swipes by property
    List<Swipe> findByPropertyList(PropertyList propertyList);

    // Find swipes by swiper and status
    List<Swipe> findBySwiperAndStatus(User swiper, Swipe.Status status);

    // Find swipes by target user and status
    List<Swipe> findByTargetUserAndStatus(User targetUser, Swipe.Status status);

    // Find swipes by property and status
    List<Swipe> findByPropertyListAndStatus(PropertyList propertyList, Swipe.Status status);

    // Find swipes by swiper and target user
    List<Swipe> findBySwiperAndTargetUser(User swiper, User targetUser);

    // Find swipes by swiper and property
    List<Swipe> findBySwiperAndPropertyList(User swiper, PropertyList propertyList);
}
