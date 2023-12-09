package com.shreyas.customer;

import com.shreyas.exception.DuplicateResourceException;
import com.shreyas.exception.RequestValidationException;
import com.shreyas.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc")CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Long customerId) {
        return customerDAO.selectCustomerById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] does not exist".formatted(customerId))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
//        Check email exist
        if(customerDAO.existCustomerByEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "Customer with email already exist");
        }
//        If not add customer
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDAO.existCustomerById(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] does not exist".formatted(customerId));
        }
        customerDAO.removeCustomerById(customerId);
    }

    public void updateCustomerById(Long customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomer(customerId);
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

        if(!change) {
            throw new RequestValidationException("No data change found");
        }

        customerDAO.updateCustomer(customer);

    }
}
