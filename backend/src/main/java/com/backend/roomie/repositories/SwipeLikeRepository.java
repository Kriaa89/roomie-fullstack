package com.backend.roomie.repositories;

import com.backend.roomie.models.SwipeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwipeLikeRepository extends JpaRepository<SwipeLike, Long> {
    List<SwipeLike> findBySwiperId(Long swiperId);
    List<SwipeLike> findBySwipedId(Long swipedId);
    Optional<SwipeLike> findBySwiperIdAndSwipedId(Long swiperId, Long swipedId);
    List<SwipeLike> findByStatus(SwipeLike.SwipeStatus status);
    List<SwipeLike> findBySwipedIdAndStatus(Long swipedId, SwipeLike.SwipeStatus status);
    List<SwipeLike> findBySwiperIdAndStatus(Long swiperId, SwipeLike.SwipeStatus status);
}