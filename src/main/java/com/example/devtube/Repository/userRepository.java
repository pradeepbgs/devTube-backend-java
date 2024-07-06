package com.example.devtube.Repository;

import org.springframework.data.repository.CrudRepository;

import com.example.devtube.models.User;

public interface userRepository extends CrudRepository<User,Integer> {
    boolean existsByUsernameOrEmail(String username, String email);
    User findUserByEmailOrUsername(String email, String username);
}
