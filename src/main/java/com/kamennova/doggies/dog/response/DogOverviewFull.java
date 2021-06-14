package com.kamennova.doggies.dog.response;

import com.kamennova.doggies.dog.Dog;
import com.kamennova.doggies.dog.DogBreed;

public class DogOverviewFull extends DogOverview {
    public String name;

    public DogOverviewFull(DogBreed breed, int age, Dog.Sex sex, String name, Long id){
        super(breed, age, sex, id);
        this.name = name;
    }
}
