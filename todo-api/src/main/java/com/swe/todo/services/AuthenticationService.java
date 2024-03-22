package com.swe.todo.services;

import com.swe.todo.models.User;
import com.swe.todo.repositories.UserRepository;
import com.swe.todo.requests.LoginRequest;
import com.swe.todo.requests.RegisterUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signUp(RegisterUserRequest request) throws Exception {
        var existsUser = userRepository.findByEmail(request.getEmail());
        if (existsUser.isPresent()) {
            throw new Exception("The email already taken");
        }
        final var password = passwordEncoder.encode(request.getPassword());
        var user = new User(UUID.randomUUID().toString(), request.getEmail(), password);

        return userRepository.save(user);
    }

    public User authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        return userRepository.findByEmail(request.getEmail())
                .orElseThrow();
    }
}
