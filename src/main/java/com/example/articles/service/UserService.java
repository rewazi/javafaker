package com.example.articles.service;

import com.example.articles.entities.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User updatedUser);
   

 



    void deleteUser(UUID id);

    void updateUser(UUID id, User user);

    Optional<User> getUserById(UUID id);

    void deleteUser(Long id);
}
