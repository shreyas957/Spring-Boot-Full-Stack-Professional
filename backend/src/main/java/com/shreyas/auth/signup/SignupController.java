package com.shreyas.auth.signup;

import com.shreyas.customer.CustomerRegistrationRequest;
import com.shreyas.customer.CustomerService;
import com.shreyas.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a Rest Controller for handling signup requests.
 * It is mapped to the "api/v1/auth/signup" URL.
 */
@RestController("api/v1/auth/signup")
public class SignupController {
    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    /**
     * Constructor for the SignupController.
     *
     * @param jwtUtil utility for handling JSON Web Tokens.
     */
    public SignupController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * This method handles POST requests for signup.
     * It adds a new user to the database. The request body should contain the new user's details(Unique email).
     * It generates a JWT token for the new user and returns it in the Authorization header.
     *
     * @param request the request body containing the new user's details.
     * @return a ResponseEntity with the JWT token in the Authorization header.
     */
    @PostMapping
    public ResponseEntity<?> signup(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }
}