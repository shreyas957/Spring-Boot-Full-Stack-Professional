package com.shreyas.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long customerId);
    boolean existCustomerByEmail(String email);
    void insertCustomer(Customer customer);
    boolean existCustomerById(Long customerId);
    void removeCustomerById(Long CustomerId);
    void updateCustomer(Customer customer);

}
