package com.kamennova.doggies.dog;

import javax.persistence.*;

@Entity
@Table(name = "dog_breed")
public class DogBreed {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private String imageSrc;

    public void setName(String name) {
        this.name = name;
    }

    public void setImageSrc(String src) {
        this.imageSrc = src;
    }

    public String getName(){
        return this.name;
    }

    public String getImageSrc(){
        return this.imageSrc;
    }

    DogBreed() {
    }
}
