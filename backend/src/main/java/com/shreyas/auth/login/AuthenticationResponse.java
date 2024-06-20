package com.shreyas.auth.login;

import com.shreyas.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
