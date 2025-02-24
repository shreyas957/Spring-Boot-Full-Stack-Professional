package com.shreyas.customer;

import com.shreyas.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

//    @RequestMapping is same as GetMapping with just only path as shorter version

    @RequestMapping(
            method = RequestMethod.GET
    )
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustomer(
            @PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(
            @RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(
            @PathVariable("customerId") Long customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("update/{customerId}")
    public void updateCustomer(
            @PathVariable("customerId") Long customerId,
            @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomerById(customerId, customerUpdateRequest);
    }

    @PostMapping(value = "{customerId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCustomerProfileImage(
            @PathVariable("customerId") Long customerId,
            @RequestParam("file") MultipartFile multipartFile) {
        customerService.uploadCustomerProfileImage(customerId, file);
    }

    @GetMapping("{customerId}/profile-image")
    public byte[] downloadCustomerProfileImage(
            @PathVariable("customerId") Long customerId) {
        return customerService.getCustomerProfileImage(customerId);
    }
}
