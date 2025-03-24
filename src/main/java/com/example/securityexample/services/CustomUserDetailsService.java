package com.example.securityexample.services;

import com.example.securityexample.entities.CustomUserDetails;
import com.example.securityexample.entities.User;
import com.example.securityexample.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        user.setPassword(user.getPassword().trim());
        System.out.println(user.getUsername() + ' ' + user.getEmail() + ' ' + user.getPassword());
        return CustomUserDetails.build(user);
    }

}
