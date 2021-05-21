package com.kamennova.doggies.dog;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "dog")
public class Dog {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String name;

    Dog(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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