package com.kamennova.doggies.dog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DogService {
    @Autowired
    DogRepository repository;

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
}
