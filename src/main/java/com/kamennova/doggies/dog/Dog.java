package com.kamennova.doggies.dog;

import com.kamennova.doggies.user.User;

import javax.persistence.*;
import java.time.Year;
import java.util.Objects;

@Entity
@Table(name = "dog")
public class Dog {
    public static enum Sex {
        Male,
        Female
    }

    public Dog() {
    }

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Integer yearBorn;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;

    Dog(String name) {
        this.name = name;
    }

    Dog(String name, Sex sex, int yearBorn, DogBreed breed, User owner) {
        this.name = name;
        this.sex = sex;
        this.yearBorn = yearBorn;
        this.breed = breed;
        this.owner = owner;
    }

    @ManyToOne(targetEntity = DogBreed.class)
    @JoinColumn(name = "breed_id")
    private DogBreed breed;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public DogBreed getBreed() {
        return this.breed;
    }

    public void setBreed(DogBreed breed) {
        this.breed = breed;
    }

    public Sex getSex() {
        return this.sex;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return this.owner;
    }

    public int getFullYears() {
        return Year.now().getValue() - this.yearBorn;
    }

    public void setYearBorn(int year) {
        this.yearBorn = year;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Dog))
            return false;
        Dog dog = (Dog) o;
        return Objects.equals(this.id, dog.id) && Objects.equals(this.name, dog.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Dog{" + "id=" + this.id + ", description='" + this.name + '}';
    }
}