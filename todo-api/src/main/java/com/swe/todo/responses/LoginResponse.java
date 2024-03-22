package com.swe.todo.responses;

import lombok.Getter;

import java.util.Date;

@Getter
public class LoginResponse {
    private final String email;
    private final String token;
    private final Date expiresAt;

    public LoginResponse(String email, String token, Date expiresAt) {
        this.email = email;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
