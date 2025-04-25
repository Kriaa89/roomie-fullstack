package com.backend.roomie.repositories;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing property listings
 * Extends JpaRepository to inherit basic CRUD operations
 */
@Repository
public interface PropertyRepository extends JpaRepository<PropretyList, Long> {
    
    /**
     * Find all properties owned by a specific user
     * @param owner the user who owns the properties
     * @return list of properties owned by the user
     */
    List<PropretyList> findByOwner(User owner);
    
    /**
     * Find all properties with a specific availability status
     * @param availability true for available properties, false for unavailable
     * @return list of properties with the specified availability
     */
    List<PropretyList> findByAvailability(boolean availability);
}