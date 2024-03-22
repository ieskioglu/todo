package com.swe.todo.controllers;

import com.swe.todo.models.User;
import com.swe.todo.requests.LoginRequest;
import com.swe.todo.requests.RegisterUserRequest;
import com.swe.todo.responses.LoginResponse;
import com.swe.todo.services.AuthenticationService;
import com.swe.todo.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthController(
            JwtService jwtService,
            AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserRequest request) throws Exception {
        var registeredUser = authenticationService.signUp(request);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        var authenticatedUser = authenticationService.authenticate(request);

        var claims = new HashMap<String, Object>();
        claims.put("id", authenticatedUser.getId());
        var token = jwtService.generateToken(claims, authenticatedUser.getEmail());

        var expiresAt = jwtService.extractExpiration(token);

        return ResponseEntity.ok(new LoginResponse(authenticatedUser.getEmail(), token, expiresAt));
    }
}
