package com.example.securityexample.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String username, String password, String email, String firstname, String lastname, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.authorities = authorities;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails build(User user) {
       //List<GrantedAuthority> authority = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority(user.getRole().name()));
       return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getFirstname(), user.getLastname(), authority);
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }



    public String fullname(){
        return firstname + " " + lastname;
    }
}
