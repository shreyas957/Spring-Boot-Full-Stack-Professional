package com.shreyas.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


// Manage our entity for CRUD operations
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    // JPQL
    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Long customerId);

    Optional<Customer> findCustomerByEmail(String email);

}
