package com.backend.roomie.repositories;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<PropretyList, Long> {
    List<PropretyList> findByOwner(User owner);
    List<PropretyList> findByAvailability(boolean availability);
    List<PropretyList> findByType(String type);
    List<PropretyList> findByLocationContaining(String location);
}
