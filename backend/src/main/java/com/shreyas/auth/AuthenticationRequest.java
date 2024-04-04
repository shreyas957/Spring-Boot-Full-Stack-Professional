package com.shreyas.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
