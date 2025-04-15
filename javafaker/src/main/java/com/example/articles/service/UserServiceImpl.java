package com.example.articles.service;

import com.example.articles.entities.User;
import com.example.articles.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) {
        user.setPassword(encoder().encode(user.getPassword()));
        userRepository.save(user);
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует!");
        }
        // Или используйте своё исключение и перехватывайте его
        return userRepository.save(user);
    }



@Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setBio(updatedUser.getBio());
                    user.setImageUrl(updatedUser.getImageUrl());
                    // Обновляем пароль напрямую — он будет виден и сохраняться в виде открытого текста
                    user.setPassword(updatedUser.getPassword());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
