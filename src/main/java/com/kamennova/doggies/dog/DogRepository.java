package com.kamennova.doggies.dog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository  extends JpaRepository<Dog, Long> {
}
