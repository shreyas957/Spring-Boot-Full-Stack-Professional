package com.shreyas.customer;

import org.springframework.data.jpa.repository.JpaRepository;


// Manage our entity for CRUD operations
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    // JPQL
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Long customerId);

}
