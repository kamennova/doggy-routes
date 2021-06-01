package com.kamennova.doggies.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository repository;
    private final UserModelAssembler assembler;

    UserController(UserRepository repository, UserModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("")
    CollectionModel<EntityModel<User>> all() {
        List<EntityModel<User>> users = repository.findAll()
                .stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<User> one(@PathVariable Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));

        return assembler.toModel(user);
    }

    @PostMapping("")
    String newUser(@RequestBody Map<String, Object> user, Errors validation) {
        final String email = user.get("email").toString();
        final String password = user.get("password").toString();

        Optional<User> existingEmail = repository.findByEmail(email);

        if (existingEmail.isPresent()) {
            validation.rejectValue("email", "user.email", "Акаунт з таким емейлом уже існує");
        }

        if (validation.hasErrors()) {
//            m.addAttribute("errors", validation);
//            m.addAttribute("user", user);
            return "users/create";
        }

        final User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));

        repository.save(newUser);

        return "redirect:/";
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        User updatedUser = repository.findById(id)
                .map(user -> {
                    user.setEmail(newUser.getEmail());
                    user.setPasswordHash(newUser.getPasswordHash());
                    return repository.save(user);
                })
                .orElseGet(() -> repository.save(newUser));

        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
