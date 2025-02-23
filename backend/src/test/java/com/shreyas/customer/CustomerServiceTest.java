package com.shreyas.customer;

import com.shreyas.exception.DuplicateResourceException;
import com.shreyas.exception.RequestValidationException;
import com.shreyas.exception.ResourceNotFoundException;
import com.shreyas.s3.S3Buckets;
import com.shreyas.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)     // Using this annotation I don't need to initialize and close the mock manually
class CustomerServiceTest {
    private CustomerService underTest;
    @Mock
    private CustomerDAO customerDAO;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO, passwordEncoder, customerDTOMapper, s3Service, s3Buckets);
    }

    @Test
    void getAllCustomers() {
        // Given

        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDAO).selectAllCustomers();      // Because of static import only verify is used
    }

    @Test
    void getCustomer() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void WillThrowExceptionWhenCustomerReturnEmptyOptional() {
        // Given
        long id = 10;
        // Below we are telling method what to do when invoked-->
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] does not exist".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDAO.existCustomerByEmail(email)).thenReturn(false);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Alex", email, "password", 19, Gender.MALE
        );

        String hashPassword = passwordEncoder.encode(customerRegistrationRequest.password());

        when(passwordEncoder.encode(customerRegistrationRequest.password())).thenReturn(hashPassword);

        // When
        underTest.addCustomer(customerRegistrationRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRegistrationRequest.email());
        assertThat(capturedCustomer.getPassword()).isEqualTo(hashPassword);
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRegistrationRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customerRegistrationRequest.gender());

    }

    @Test
    void WillThrowExceptionWhenEmailExistWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDAO.existCustomerByEmail(email)).thenReturn(true);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Alex", email, "password", 19, Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email already exist");

        // Then
        verify(customerDAO, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        // Given
        long id = 10;
        when(customerDAO.existCustomerById(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDAO).removeCustomerById(id);
    }

    @Test
    void WillThrowExceptionWhenDeleteCustomerByIdDoesNotExist() {
        // Given
        long id = 10;
        when(customerDAO.existCustomerById(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] does not exist".formatted(id));

        // Then
        verify(customerDAO, never()).removeCustomerById(id);
    }

    @Test
    void WillUpdateAllCustomerPropertiesById() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                "Alex", "alex@gmail.com", "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alex123@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", newEmail, 29
        );

//        when(customerDAO.existCustomerByEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer captured = customerArgumentCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(updateRequest.name());
        assertThat(captured.getEmail()).isEqualTo(updateRequest.email());
        assertThat(captured.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void WillUpdateCustomerNamePropertyById() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                "Alex", "alex@gmail.com", "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", null, null
        );

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer captured = customerArgumentCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(updateRequest.name());
        assertThat(captured.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captured.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void WillUpdateCustomerEmailPropertyById() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                "Alex", "alex@gmail.com", "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alex123@amezon.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer captured = customerArgumentCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(customer.getName());
        assertThat(captured.getEmail()).isEqualTo(newEmail);
        assertThat(captured.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void WillUpdateCustomerAgePropertyById() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                "Alex", "alex@gmail.com", "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 35
        );

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer captured = customerArgumentCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(customer.getName());
        assertThat(captured.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captured.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void WillThrowExceptionEmailAlreadyExistWhileUpdateCustomer() {
        // Given
        long id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                "Alex", email, "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alex John", email, 21
        );
        when(customerDAO.existCustomerByEmail(email)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");

        // Then
        verify(customerDAO, never()).updateCustomer(any());

    }

    @Test
    void WillThrowExceptionWhenNoChangesWhileUpdateCustomer() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                "Alex", "alex@gmail.com", "password", 29,
                Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data change found");

        // Then
        verify(customerDAO, never()).updateCustomer(any());


    }
}