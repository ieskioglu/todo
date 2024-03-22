package com.swe.todo.securities;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SignedUserHelper {
    public static SignedUser user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            return (SignedUser) authentication.getPrincipal();
        } catch (ClassCastException exception) {
            throw exception;
        }
    }

    public static String userId() {
        return user().getUserId();
    }
}
