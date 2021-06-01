package com.kamennova.doggies;

import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserRepository;
import com.kamennova.doggies.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private final UserRepository repository;
    private final UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    AuthController(UserRepository repo, UserService service) {
        this.repository = repo;
        this.service = service;
    }

    @PostMapping("/signIn")
    @ResponseBody
    public HashMap<String, String> login(@RequestBody Map<String, String> body) {
        final HashMap<String, String> response = new HashMap<>();

        final String email = body.get("email");
        final String password = body.get("password");

        final Optional<User> user = repository.findByEmail(email);

        if (user.isEmpty()) {
            response.put("error", "Користувача з таким емейлом не існує");
            return response;
        }

        if (!passwordEncoder.matches(password, user.get().getPasswordHash())) {
            response.put("error", "Неправильний пароль");
            return response;
        }

        service.authenticate(user.get());
        response.put("status", "ok");

        return response;
    }

//    @PostMapping("/auth/signOut")
//    public void signOut()

}
