package com.kamennova.doggies.dog;

public class DogNotFoundException extends RuntimeException {

    DogNotFoundException(Long id) {
        super("Could not find dog " + id);
    }
}