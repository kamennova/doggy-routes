package com.kamennova.doggies.dog;

import javax.persistence.*;

@Entity
@Table(name = "dog_breed")
public class DogBreed {

    DogBreed() {
    }

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Short id;

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

    public void setId(Short id){
        this.id = id;
    }

    public Short getId(){
        return this.id;
    }
}
