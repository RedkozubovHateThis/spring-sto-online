package io.swagger.service;

import io.swagger.model.User;

import java.util.List;

public interface UserService {

    User save(User user);
    List<User> findAll();
    // void delete(long id);
}