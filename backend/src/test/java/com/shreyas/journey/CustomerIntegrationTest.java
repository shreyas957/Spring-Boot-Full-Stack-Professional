package com.shreyas.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.shreyas.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";


    @Test
    void canRegisterCustomer() {
        // Create Registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.firstName() + "." + fakerName.lastName() + "@xxx.com";
        int age = RANDOM.nextInt(10, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );

        // Send a post request
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(CUSTOMER_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                        .exchange()     //--> Send request
                        .expectStatus()
                        .isOk()
                        .returnResult(void.class)
                        .getResponseHeaders()
                        .get(HttpHeaders.AUTHORIZATION))
                .get(0);

        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerDTO expected = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expected);

        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(expected);
    }

    @Test
    void canDeleteCustomer() {
        // Create Registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email1 = fakerName.firstName() + "." + fakerName.lastName() + "@xxx.com";
        int age = RANDOM.nextInt(10, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest registrationRequest1 = new CustomerRegistrationRequest(
                name, email1, "password", age, gender
        );
        CustomerRegistrationRequest registrationRequest2 = new CustomerRegistrationRequest(
                name, email1 + ".in", "password", age, gender
        );

        // Send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest1), CustomerRegistrationRequest.class)
                .exchange()     //--> Send request
                .expectStatus()
                .isOk();


        // Send a post request to create customer 2
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(CUSTOMER_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(registrationRequest2), CustomerRegistrationRequest.class)
                        .exchange()     //--> Send request
                        .expectStatus()
                        .isOk()
                        .returnResult(void.class)
                        .getResponseHeaders()
                        .get(HttpHeaders.AUTHORIZATION))
                .get(0);


        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present and get id to delete the customer
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email1))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        // Delete customer 1
        // Using 2nd customer JwtToken we delete 1st customer. We cant delete the same one because the token expires
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        // Get Customer By id (We want 404 not found because the customer of that id is deleted
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // Create Registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.firstName() + "." + fakerName.lastName() + "@xxx.com";
        int age = RANDOM.nextInt(10, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest registrationRequest1 = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );

        CustomerRegistrationRequest registrationRequest2 = new CustomerRegistrationRequest(
                name, email + ".uk", "password", age, gender
        );

        // Add customer 1 and then update
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest1), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send a post request to add customer 2
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(CUSTOMER_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(registrationRequest2), CustomerRegistrationRequest.class)
                        .exchange()     //--> Send request
                        .expectStatus()
                        .isOk()
                        .returnResult(void.class)
                        .getResponseHeaders()
                        .get(HttpHeaders.AUTHORIZATION))
                .get(0);

        // get all customers by customer 2 token
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present and get id to update the customer
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        String tempEmail = faker.name() + "@gmail.com";
        String tempName = faker.name().fullName();
        int tempAge = RANDOM.nextInt(18, 100);

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                tempName, tempEmail, tempAge
        );

        webTestClient.put()
                .uri(CUSTOMER_URI + "/update/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Get the updated customer back(By customer 2 token) and compare it to updateRequest to check
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        CustomerDTO expected = new CustomerDTO(
                id, tempName, tempEmail, gender, tempAge, List.of("ROLE_USER"), tempEmail
        );
        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
