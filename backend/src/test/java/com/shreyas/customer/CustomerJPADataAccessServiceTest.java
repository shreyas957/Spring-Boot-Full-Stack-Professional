package com.shreyas.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    @Mock   // Mocked Dependency
    private CustomerRepository customerRepository;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        // Initialize the mock
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // When --> we call method underTest.selectAllCustomers, we want to make sure that customerRepository.findAll() --> gets invoked
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());
        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = underTest.selectAllCustomers();

        // Then
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());
        assertThat(pageArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(10));
    }

    @Test
    void selectCustomerById() {
        // Given
        long id = 1;

        // When
        underTest.selectCustomerById(id);

        // Then
        verify(customerRepository).findById(id);
    }

    @Test
    void existCustomerByEmail() {
        // Given
        String email = "shevaleshreyas2003@gmail.com";

        // When
        underTest.existCustomerByEmail(email);

        // Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void insertCustomer() {
        // Given
        Faker faker = new Faker();
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                "password", 25,
                Gender.MALE);

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existCustomerById() {
        // Given
        long id = 12;

        // When
        underTest.existCustomerById(id);

        // Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void removeCustomerById() {
        // Given
        long id = 2;

        // When
        underTest.removeCustomerById(id);

        // Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        Faker faker = new Faker();
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                "password", 67,
                Gender.MALE);

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }
}