package com.shreyas.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                         CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                """;

        // RowMapper is a Lambda which returns the customer by grabbing the column contents for each row
//        RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
//            Customer customer = new Customer(
//                    rs.getLong("id"),
//                    rs.getString("name"),
//                    rs.getString("email"),
//                    rs.getInt("age")
//            );
//            return customer;
//
//        };

        // Instead of doing like above we have used RowMapper class below
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, customerId)
                .stream().
                findFirst();
    }

    @Override
    public boolean existCustomerByEmail(String email) {
        // The below sql query gives us how many number of rows are there with the particular email which is stored into variable count
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    @Override
    public void insertCustomer(Customer customer) {
        // Values will be passed to ? from customer parameter
        var sql = """
                INSERT INTO customer(name, email, age, gender)
                VALUES(?,?,?,?)
                """;
        int result = jdbcTemplate.update(
                sql,
                // Below parameter values will be passed to sql wildcard above
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name()
        );
        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existCustomerById(Long customerId) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        return count != null && count > 0;
    }

    @Override
    public void removeCustomerById(Long customerId) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("removeCustomerById result = " + result);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = "UPDATE customer SET name  = ? WHERE id = ?";
            int result = jdbcTemplate.update(sql, customer.getName(), customer.getId());
            System.out.println("update customer name result = " + result);
        }

        if (customer.getEmail() != null) {
            String sql = "UPDATE customer SET email  = ? WHERE id = ?";
            int result = jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
            System.out.println("update customer email result = " + result);
        }

        if (customer.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(sql, customer.getAge(), customer.getId());
            System.out.println("update customer age result = " + result);
        }
    }
}
