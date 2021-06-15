package com.kamennova.doggies.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public String validate(String email, String password){
        Optional<User> existingEmail = userRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            return "Акаунт з таким емейлом уже існує";
        } else if (password.length() < 5) {
            return "Пароль має містити хоча б 5 символів";
        }

        return "";
    }
}
