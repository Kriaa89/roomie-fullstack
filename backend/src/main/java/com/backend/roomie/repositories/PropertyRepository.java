package com.backend.roomie.repositories;

import com.backend.roomie.models.PropertyList;
import com.backend.roomie.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyList, Long> {
    List<PropertyList> findByOwner(User owner);
    List<PropertyList> findByAvailability(boolean availability);
    List<PropertyList> findByType(String type);
    List<PropertyList> findByLocationContaining(String location);
}
