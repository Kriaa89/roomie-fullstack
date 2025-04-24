package com.backend.roomie.repositories;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // Find roles by user
    List<UserRole> findByUser(User user);
    
    // Find roles by user id
    List<UserRole> findByUserId(Long userId);
}