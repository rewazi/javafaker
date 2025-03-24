package com.example.articles.service;

import com.example.articles.entities.User;
import com.example.articles.repositories.UserRepository;
import com.example.articles.roles.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Существующие методы
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
        // createUser может отвечать за создание пользователя без привязки к логике регистрации,
        // или наоборот вы можете здесь делать ту же логику. Это на ваше усмотрение.
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updatedUser.getUsername());
                    existing.setEmail(updatedUser.getEmail());
                    existing.setBio(updatedUser.getBio());
                    // Пароль, роль и т.д. при апдейте тоже можно обновлять
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: id=" + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(UUID id, User user) {
        // Если вам нужен метод для UUID, реализуйте аналогично
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        // Аналогично, если вам нужно по UUID
        return Optional.empty();
    }

    // Новый метод для регистрации
    @Override
    public User registerNewUser(User user) {
        // Проверяем, нет ли уже пользователя с таким email
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Пользователь с таким email уже существует");
                });

        // Хешируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ставим роль, если не указана
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        // Сохраняем
        return userRepository.save(user);
    }
}
