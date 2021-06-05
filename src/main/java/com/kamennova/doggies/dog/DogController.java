package com.kamennova.doggies.dog;

import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dogs")
public class DogController {
    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private DogService dogService;

    @Autowired
    private DogBreedRepository breedRepository;

    @GetMapping("/{id}")
    Dog one(@PathVariable Long id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));
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
        final String error = dogService.validate(name, breed, yearBorn);

        if (!error.isEmpty()) {
            res.put("error", error);
            return res;
        }

        final Dog newDog = dogService.save(name, breed.get(), yearBorn, sex, user);
        res.put("status", "ok");
        res.put("id", newDog.getId().toString());

        return res;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    Model deleteDog(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        Optional<Dog> dog = dogRepository.findById(id);

        if (dog.isEmpty() || !dog.get().getOwner().getId().equals(user.getId())) {
            model.addAttribute("error", "Собаки з таким id не знайдено");
            return model;
        }

        dogService.delete(dog.get());
        model.addAttribute("status", "ok");

        return model;
    }
}
