package com.kamennova.doggies.dog;

import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dogs")
public class DogController {
    @Autowired
    private DogRepository repository;

    @Autowired
    private DogService service;

    @Autowired
    private DogBreedRepository breedRepository;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@RequestBody NewDogReq dog, @AuthenticationPrincipal User user) {
        final HashMap<String, Object> res = new HashMap<>();

        final Optional<DogBreed> breed = breedRepository.findById(dog.breedId);
        final String error = service.validate(dog.name, breed, dog.yearBorn);

        if (!error.isEmpty()) {
            res.put("error", error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        final Dog newDog = service.save(dog.name, breed.get(), dog.yearBorn, dog.sex, user);
        res.put("id", newDog.getId());

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        Dog dog = repository.findById(id).orElseThrow(() -> new DogNotFoundException(id));
        service.delete(dog);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
