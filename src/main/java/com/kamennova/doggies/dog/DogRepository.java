package com.kamennova.doggies.dog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByOwnerId(Long ownerId);

    @Query(value="SELECT count(*) > 0 FROM dog WHERE owner_id = ?1", nativeQuery = true)
    boolean userHasDogs(Long userId);

    Optional<Dog> findByName(String name);
}
