package com.shreyas.customer;

import com.github.javafaker.Faker;
import com.shreyas.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        // Before every test we get fresh new object of CustomerJDBCDataAccessService
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Faker faker = new Faker();
        // Given
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                "password", 29,
                Gender.MALE);
        underTest.insertCustomer(customer);
        // When
        List<Customer> actual = underTest.selectAllCustomers();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        Faker faker = new Faker();
        // Given
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 29,
                Gender.MALE);
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    public void willReturnEmptyWhenSelectCustomerById() {
        // Given
        long id =  -1;

        // When
        var actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existCustomerByEmail() {
        Faker faker = new Faker();
        // Given
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 29,
                Gender.MALE);
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    public void existCustomerByEmailReturnFalseWhenDoesNotExist() {
        Faker faker = new Faker();
        // Given
        String email = faker.internet().safeEmailAddress();

        // When
        boolean actual = underTest.existCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void insertCustomer() {
        // Given

        // When

        // Then
    }

    @Test
    void existCustomerById() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.existCustomerById(id);

        // Then
        assertThat(actual).isTrue();
    }

    void existCustomerWithIdReturnFalseWhenNotPresent() {
        // Given
        Long id = (long) -1;

        // When
        var actual = underTest.existCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void removeCustomerById() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.removeCustomerById(id);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomer() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        // We can have separate test for each property of customer
        var newName = "Foo";
        var newEmail = faker.internet().safeEmailAddress();
        var newAge = 21;
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setEmail(newEmail);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c ->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            // Email and Age will be same as of old customer
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateCustomerName() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var newName = "Foo";
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c ->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var newEmail = faker.internet().safeEmailAddress();
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c ->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail() .equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var newAge = 21;
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c ->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }
}