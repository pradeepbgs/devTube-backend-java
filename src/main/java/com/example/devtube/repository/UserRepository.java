package com.example.devtube.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.devtube.entities.User;

public interface UserRepository extends CrudRepository<User,Integer> {
    boolean existsByUsernameOrEmail(String username, String email);
    // User findUserByEmailOrUsername(String username);
    User findByUsername(String username);
}
