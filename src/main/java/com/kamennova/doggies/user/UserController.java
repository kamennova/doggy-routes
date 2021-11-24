package com.kamennova.doggies.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
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
    ResponseEntity<Map<String, String>> create(@RequestBody NewUserReq user) {
        Map<String, String> res = new HashMap<>();
        final String error = service.validate(user.email, user.password);

        if (!error.isEmpty()) {
            res.put("error", error);
            return ResponseEntity.badRequest().body(res);
        }

        byte[] address = new byte[0];
        try {
            if (user.address != null) {
                address = service.encryptUserAddress(user.address);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        repository.save(new User(user.email, passwordEncoder.encode(user.password), address));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
