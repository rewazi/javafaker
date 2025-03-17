package com.example.articles.service;

import com.example.articles.entities.User;
import com.example.articles.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        System.out.println("Сохранение пользователя в БД: " + user);
        User savedUser = userRepository.save(user);
        System.out.println("Пользователь сохранен: " + savedUser);
        return savedUser;
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setBio(updatedUser.getBio());
                    user.setImageUrl(updatedUser.getImageUrl());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public void updateUser(UUID id, User user) {

    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {

    }


}
