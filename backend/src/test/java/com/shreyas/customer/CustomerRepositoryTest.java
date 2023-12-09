package com.shreyas.customer;

import com.github.javafaker.Faker;
import com.shreyas.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        // If we want fresh database every time we can use underTest.deleteAll(); in setUp
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20,
                Gender.MALE);
        underTest.save(customer);
        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailIsNotPresent() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();

        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20,
                Gender.MALE);
        underTest.save(customer);
        Long id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isTrue();
    }
    @Test
    void existsCustomerByIdFailWhenIdIsNotPresent() {
        // Given
        long id = -1;

        // When
        boolean actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }

}