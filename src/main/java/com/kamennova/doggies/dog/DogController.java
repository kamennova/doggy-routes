package com.kamennova.doggies.dog;

import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserRepository;
import com.kamennova.doggies.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dogs")
public class DogController {
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DogModelAssembler assembler;
    private final DogBreedRepository breedRepository;

    @Autowired
    DogController(DogRepository dogRepository, DogModelAssembler assembler, UserService userService, UserRepository userRepository, DogBreedRepository breedRepository) {
        this.dogRepository = dogRepository;
        this.assembler = assembler;
        this.userService = userService;
        this.userRepository = userRepository;
        this.breedRepository = breedRepository;
    }

    @GetMapping("/{id}")
    EntityModel<Dog> one(@PathVariable Long id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));

        return assembler.toModel(dog);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, String> addDog(@RequestBody Map<String, Object> dog, @AuthenticationPrincipal User user) {
        final HashMap<String, String> res = new HashMap<>();

        final String name = dog.get("name").toString();
        final Dog.Sex sex = dog.get("sex").toString().equals("female") ? Dog.Sex.Female : Dog.Sex.Male;
        final int yearBorn = Integer.parseInt(dog.get("yearBorn").toString());
        final Short breedId = Short.parseShort(dog.get("breedId").toString());

        final Optional<DogBreed> breed = breedRepository.findById(breedId);
        System.out.println(user);

        String error = "";
        if (name.length() == 0) {
            error = "Введіть імʼя собаки";
        } else if (yearBorn > Year.now().getValue() || yearBorn < Year.now().getValue() - 40) {
            error = "Введіть коректний рік народження";
        } else if (breed.isEmpty()) {
            error = "Введіть коректну породу собаки";
        }

        if (!error.isEmpty()) {
            res.put("error", error);
            return res;
        }

        Dog newDog = new Dog(name, sex, yearBorn, breed.get(), user);
        dogRepository.save(newDog);

        res.put("status", "ok");
        res.put("id", newDog.getId().toString());

        return res;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteDog(@PathVariable Long id, @AuthenticationPrincipal User user) {
        dogRepository.deleteById(id);

        // todo if no more dogs, mark all user's routes inactive
        if (user.getDogs().isEmpty()) {

        }

        return ResponseEntity.noContent().build();
    }
}
