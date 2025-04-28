package com.backend.roomie.repositories;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // Find roles by user
    List<UserRole> findByUser(User user);
    // Find roles by user ID
    List<UserRole> findByUserId(Long userId);

}