package com.kamennova.doggies.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserDetailsLoader implements UserDetailsService {
    private final UserRepository repository;
    private final String role;

    @Autowired
    public UserDetailsLoader(UserRepository repository) {
        this.repository = repository;
        this.role = "USER_ROLE";
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user found for " + email);
        }

        return new UserWithRole(user.get(), "USER_ROLE");
    }
}