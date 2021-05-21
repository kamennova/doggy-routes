package com.kamennova.doggies;

import com.kamennova.doggies.dog.DogRepository;
import com.kamennova.doggies.route.RouteRepository;
import com.kamennova.doggies.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, DogRepository dogRepository, RouteRepository routeRepository) {

        return args -> {
            routeRepository.findAll().forEach(route -> {
                log.info("Preloaded " + route);
            });

            dogRepository.findAll().forEach(dog -> {
                log.info("Preloaded " + dog);
            });

        };
    }
}