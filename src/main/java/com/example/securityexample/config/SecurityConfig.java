package com.example.securityexample.config;

import com.example.securityexample.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authenticationProvider(daoAuthenticationProvider())
//                .authorizeHttpRequests((request) -> request
//                        .requestMatchers("/registration").permitAll()
//                        .anyRequest().authenticated())
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        //.usernameParameter("email")
//                        .defaultSuccessUrl("/")
//                        .permitAll()
//                )
//                .logout((logout) -> logout.permitAll());
//        return http.build();
        http.authenticationProvider(daoAuthenticationProvider());

        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("/users").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(login ->
                        login.usernameParameter("email")
                                //.loginPage("/login")
                                .defaultSuccessUrl("/users")
                                .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll()
                );

        return http.build();
    }
}
