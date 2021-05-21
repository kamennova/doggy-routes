package com.kamennova.doggies.user;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    User save(User user);
}
