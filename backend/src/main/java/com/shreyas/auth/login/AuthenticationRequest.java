package com.shreyas.auth.login;

public record AuthenticationRequest(
        String username,
        String password
) {
}
