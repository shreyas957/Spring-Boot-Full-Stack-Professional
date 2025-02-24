package com.shreyas;

import com.github.javafaker.Faker;
import com.shreyas.customer.Customer;
import com.shreyas.customer.CustomerRepository;
import com.shreyas.customer.Gender;
import com.shreyas.s3.S3Buckets;
import com.shreyas.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, S3Service s3Service, S3Buckets s3Buckets) {

        return args -> {
            createRandomCustomer(customerRepository, passwordEncoder);
//            testUploadAndDownloadS3(s3Service, s3Buckets);

        };
    }

    private static void testUploadAndDownloadS3(S3Service s3Service, S3Buckets s3Buckets) {
        s3Service.putObject(s3Buckets.getCustomer(), "foo", "Hello World".getBytes());
        byte[] obj = s3Service.getObject(s3Buckets.getCustomer(), "foo");
        System.out.println(new String(obj));
    }

    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        Faker faker = new Faker();
        Random random = new Random();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        int age = random.nextInt(16, 99);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        String email = firstName.toLowerCase() + "." + lastName + "@gmail.com";
//            String password = UUID.randomUUID().toString();
        Customer customer = new Customer(
                firstName + " " + lastName,
                firstName.toLowerCase() + "." + lastName + "@gmail.com",
                passwordEncoder.encode("password"),
                age,
                gender);
        customerRepository.save(customer);
        System.out.println(email);
    }
}
