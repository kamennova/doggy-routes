package com.kamennova.doggies.dog;

import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DogController {
    private final DogRepository dogRepository;
    private final UserService userService;
    private final DogModelAssembler assembler;

    DogController(DogRepository dogRepository, DogModelAssembler assembler, UserService userService) {
        this.dogRepository = dogRepository;
        this.assembler = assembler;
        this.userService = userService;
    }

    @GetMapping("/dogs/{id}")
    EntityModel<Dog> one(@PathVariable Long id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));

        return assembler.toModel(dog);
    }

    @PostMapping("/dogs")
    ResponseEntity<?> newDog(Principal principal, @RequestBody Dog dog) {

        Optional<User> user = userService.findByEmail(principal.getName());

        if (user.isPresent()) {
            Dog newDog = dogRepository.save(new Dog(dog.getName()));

            return ResponseEntity
                    .created(
                            linkTo(methodOn(DogController.class).one(newDog.getId())).toUri()
                    )
                    .body(assembler.toModel(newDog));

        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) //
                    .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                    .body(Problem.create()
                            .withTitle("Method not allowed")
                            .withDetail("Not authorized"));
        }
    }
}
