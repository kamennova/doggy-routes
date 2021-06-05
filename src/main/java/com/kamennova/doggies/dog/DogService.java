package com.kamennova.doggies.dog;

import com.kamennova.doggies.route.RouteRepository;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DogService {
    @Autowired
    DogRepository repository;

    @Autowired
    DogBreedRepository breedRepository;

    @Autowired
    RouteRepository routeRepository;

    public List<HashMap<String, String>> getDogsInfoOfOwner(Long ownerId) {
        return repository.findByOwnerId(ownerId).stream().map(dog -> {
            final HashMap<String, String> obj = new HashMap<>();
            obj.put("name", dog.getName());
            obj.put("breedName", dog.getBreed().getName());
            obj.put("breedPic", dog.getBreed().getImageSrc());
            obj.put("sex", dog.getSex().toString());
            obj.put("age", String.valueOf(dog.getFullYears()));
            obj.put("id", dog.getId().toString());

            return obj;
        }).collect(Collectors.toList());
    }

    public List<DogBreed> getBreedsInfo() {
        return breedRepository.findAllByOrderByNameAsc();
    }

    public void delete(Dog dog) {
        final User owner = dog.getOwner();

        if (owner.getDogs().size() == 1) { // user has no more dogs
            routeRepository.hideRoutesByUserId(owner.getId());
        }

        repository.delete(dog);
    }

    public String validate(String name, Optional<DogBreed> breed, int yearBorn) {
        if (name.length() == 0) {
            return "Введіть імʼя собаки";
        } else if (yearBorn > Year.now().getValue() || yearBorn < Year.now().getValue() - 40) {
            return "Введіть коректний рік народження";
        } else if (breed.isEmpty()) {
            return "Введіть коректну породу собаки";
        }

        return "";
    }

    public Dog save(String name, DogBreed breed, int yearBorn, Dog.Sex sex, User user) {
        Dog newDog = new Dog(name, sex, yearBorn, breed, user);
        repository.save(newDog);
        routeRepository.showRoutesByUserId(user.getId());

        return newDog;
    }
}
