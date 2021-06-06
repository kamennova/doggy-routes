package com.kamennova.doggies.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;

    @PostMapping("")
    @ResponseBody
    HashMap<String, String> newUser(@RequestBody Map<String, Object> user) {
        HashMap<String, String> res = new HashMap<>();
        final String email = user.get("email").toString();
        final String password = user.get("password").toString();
        final String error = service.validate(email, password);

        if (!error.isBlank()) {
            res.put("error", error);
        } else {
            final User newUser = new User(email, passwordEncoder.encode(password));
            repository.save(newUser);
            res.put("status", "ok");
        }

        return res;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
