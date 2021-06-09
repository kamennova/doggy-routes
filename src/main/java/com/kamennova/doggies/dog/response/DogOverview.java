package com.kamennova.doggies.dog.response;

import com.kamennova.doggies.dog.Dog;
import com.kamennova.doggies.dog.DogBreed;

public class DogOverview {
    public BreedOverview breed;
    public int age;
    public char sex;

    public DogOverview(DogBreed breed, int age, Dog.Sex sex) {
        this.breed = breed.getOverview();
        this.age = age;
        this.sex = sex == Dog.Sex.Female ? 'f' : 'm';
    }
}
