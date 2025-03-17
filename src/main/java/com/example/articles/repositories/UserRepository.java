package com.example.articles.repositories;

import com.example.articles.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);


    Optional<User> findByUsername(String username);


    List<User> findByEmailContainingIgnoreCase(String emailPart);


    List<User> findByUsernameContainingIgnoreCase(String usernamePart);


    List<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String emailPart, String usernamePart);
}
