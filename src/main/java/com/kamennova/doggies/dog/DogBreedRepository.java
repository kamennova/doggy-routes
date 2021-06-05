package com.kamennova.doggies.dog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogBreedRepository extends JpaRepository<DogBreed,Short> {
    List<DogBreed> findAllByOrderByNameAsc();
}
