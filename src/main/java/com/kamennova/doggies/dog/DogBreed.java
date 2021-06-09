package com.kamennova.doggies.dog;

import com.kamennova.doggies.dog.response.BreedOverview;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dog_breed")
public class DogBreed implements Serializable {

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

    public BreedOverview getOverview(){
        return new BreedOverview(name, imageSrc);
    }
}
