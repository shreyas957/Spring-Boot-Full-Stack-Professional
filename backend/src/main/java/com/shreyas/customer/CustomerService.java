package com.shreyas.customer;

import com.shreyas.exception.DuplicateResourceException;
import com.shreyas.exception.RequestValidationException;
import com.shreyas.exception.ResourceNotFoundException;
import com.shreyas.s3.S3Buckets;
import com.shreyas.s3.S3Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, S3Service s3Service, S3Buckets s3Buckets) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.selectAllCustomers().stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Long customerId) {
        return customerDAO.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] does not exist".formatted(customerId))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
//        Check email exist
        if (customerDAO.existCustomerByEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "Customer with email already exist");
        }
//        If not add customer
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Long customerId) {
        checkCustomerExists(customerId);
        customerDAO.removeCustomerById(customerId);
    }

    private void checkCustomerExists(Long customerId) {
        if (!customerDAO.existCustomerById(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] does not exist".formatted(customerId));
        }
    }

    public void updateCustomerById(Long customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDAO.selectCustomerById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] does not exist".formatted(customerId))
                );
        // TODO: Check that if updated data is not null or there in no change in value
        boolean change = false;
        if (updateRequest.name() != null && !customer.getName().equals(updateRequest.name())) {
            customer.setName(updateRequest.name());
            change = true;
        }
        if (updateRequest.email() != null && !customer.getEmail().equals(updateRequest.email())) {
            customer.setEmail(updateRequest.email());
            change = true;
        }
        if (updateRequest.age() != null && !customer.getAge().equals(updateRequest.age())) {
            if (customerDAO.existCustomerByEmail(updateRequest.email())) {
                throw new DuplicateResourceException("Email is already taken");
            }
            customer.setAge(updateRequest.age());
            change = true;
        }

        if (!change) {
            throw new RequestValidationException("No data change found");
        }

        customerDAO.updateCustomer(customer);

    }

    public void uploadCustomerProfileImage(Long customerId, MultipartFile file) {
        // check customer
        checkCustomerExists(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(s3Buckets.getCustomer(), "profile-images/%s/%s".formatted(customerId, profileImageId), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: store customer id and image id in postgres db.
    }

    public byte[] getCustomerProfileImage(Long customerId) {
        // TODO: check if customer exists and image id is stored.
        var profileImageId = UUID.randomUUID().toString();
        return s3Service.getObject(s3Buckets.getCustomer(), "profile-images/%s/%s".formatted(customerId, profileImageId));
    }
}
