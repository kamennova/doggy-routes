package com.kamennova.doggies.user;

class UserNotFoundException extends RuntimeException {

    UserNotFoundException(String id) {
        super("Could not find user " + id);
    }
}