package com.kamennova.doggies.dog;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DogModelAssembler implements RepresentationModelAssembler<Dog, EntityModel<Dog>> {
    public EntityModel<Dog> toModel(Dog dog){
        EntityModel<Dog> dogEntity = EntityModel.of(dog,
                linkTo(methodOn(DogController.class).one(dog.getId())).withSelfRel()
                );

        return dogEntity;
    }
}
