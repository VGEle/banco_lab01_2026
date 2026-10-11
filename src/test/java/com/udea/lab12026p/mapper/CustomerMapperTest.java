package com.udea.lab12026p.mapper;

import com.udea.lab12026p.dto.CustomerDTO;
import com.udea.lab12026p.entity.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void toDTOMapsCustomerFields() {
        Customer customer = new Customer(1L, "1001", "Ana", "Gómez", 250.0);

        CustomerDTO result = mapper.toDTO(customer);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("1001", result.getAccountNumber()),
                () -> assertEquals("Ana", result.getFirstName()),
                () -> assertEquals("Gómez", result.getLastName()),
                () -> assertEquals(250.0, result.getBalance())
        );
    }

    @Test
    void toDTOReturnsNullForNullCustomer() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntityMapsCustomerDTOFields() {
        CustomerDTO customerDTO = new CustomerDTO(2L, "Luis", "Pérez", "1002", 400.0);

        Customer result = mapper.toEntity(customerDTO);

        assertAll(
                () -> assertEquals(2L, result.getId()),
                () -> assertEquals("1002", result.getAccountNumber()),
                () -> assertEquals("Luis", result.getFirstName()),
                () -> assertEquals("Pérez", result.getLastName()),
                () -> assertEquals(400.0, result.getBalance())
        );
    }

    @Test
    void toEntityReturnsNullForNullDTO() {
        assertNull(mapper.toEntity(null));
    }
}
