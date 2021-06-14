package com.kamennova.doggies.dog.response;

import com.kamennova.doggies.dog.Dog;
import com.kamennova.doggies.dog.DogBreed;

import java.util.Objects;

public class DogOverview {
    public Long id;
    public BreedOverview breed;
    public int age;
    public char sex;

    public DogOverview(DogBreed breed, int age, Dog.Sex sex, Long id) {
        this.breed = breed.getOverview();
        this.age = age;
        this.sex = sex == Dog.Sex.Female ? 'f' : 'm';
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DogOverview))
            return false;
        DogOverview dog = (DogOverview) o;
        return Objects.equals(this.id, dog.id);
    }
}
