package com.kamennova.doggies.dog;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DogSexConverter implements Converter<String, Dog.Sex> {
    @Override
    public Dog.Sex convert(String value) {
        return value.toLowerCase().equals("female") ? Dog.Sex.Female : Dog.Sex.Male;
    }
}