package com.example.articles.repositories;

import com.example.articles.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {





    List<User> findByEmailContainingIgnoreCase(String emailPart);


    List<User> findByUsernameContainingIgnoreCase(String usernamePart);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);


    List<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String emailPart, String usernamePart);
}
