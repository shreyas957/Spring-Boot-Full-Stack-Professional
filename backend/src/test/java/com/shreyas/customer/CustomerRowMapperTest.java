package com.shreyas.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @Mock
    private ResultSet resultSet;  // or ResultSet resultSet = mock(ResultSet.class);
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapRow() throws SQLException {
        // Given
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Shreyas");
        when(resultSet.getString("email")).thenReturn("shreyas@google.com");
        when(resultSet.getInt("age")).thenReturn(20);
        when(resultSet.getString("gender")).thenReturn("FEMALE");

        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        Customer expected = new Customer(
                1L, "Shreyas", "shreyas@google.com", 20,
                Gender.FEMALE);

        assertThat(actual).isEqualTo(expected);
    }
}