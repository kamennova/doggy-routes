package com.kamennova.doggies.dog;

import com.kamennova.doggies.dog.response.DogOverview;
import com.kamennova.doggies.route.RouteRepository;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
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

    public List<DogOverview> getDogsInfoOfOwner(Long ownerId) {
        return repository.findByOwnerId(ownerId).stream().map(Dog::getFullOverview).collect(Collectors.toList());
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
        } else if (repository.findByName(name).isPresent()){
            return "Собака з таким імʼям вже створена";
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
