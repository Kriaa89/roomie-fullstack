package com.backend.roomie.repositories;


import com.backend.roomie.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<User, Long> {
    // This method retrieves a User from the database by email
    Optional<User> findByEmail(String email);
    // Find users by location
    List<User> findByLocation(String location);
    // Find users by phone verification status
    List<User> findByPhoneVerified(Boolean verified);

}
