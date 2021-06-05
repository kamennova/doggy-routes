package com.kamennova.doggies.user;

import com.kamennova.doggies.dog.Dog;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(targetEntity = Dog.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    private Set<Dog> dogs;

    User() {
    }

    User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    User(User user) {
        this.id = user.id;
        this.passwordHash = user.passwordHash;
        this.email = user.email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String hash) {
        this.passwordHash = hash;
    }

    public void setDogs(Set<Dog> dogs) {
        this.dogs = dogs;
    }

    public Collection<Dog> getDogs() {
        return this.dogs;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", email='" + this.email + '}';
    }
}
