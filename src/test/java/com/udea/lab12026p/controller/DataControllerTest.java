package com.udea.lab12026p.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataControllerTest {

    private final DataController controller = new DataController(new ObjectMapper());

    @Test
    void exposesHealthAndVersion() {
        assertEquals("HEALTH CHECK OK!", controller.healthCheck());
        assertEquals("The actual version is 1.0.0", controller.version());
    }

    @Test
    void returnsTenNations() {
        assertEquals(10, controller.getRandomNations().size());
    }

    @Test
    void returnsTwentyCurrenciesWithThreeLetterCodes() {
        var currencies = controller.getRandomCurrencies();

        assertEquals(20, currencies.size());
        currencies.forEach(currency ->
                assertTrue(currency.get("code").asText().matches("[A-Z]{3}")));
    }

    @Test
    void returnsTwentyAviationRecords() {
        assertEquals(20, controller.getRandomAviation().size());
    }
}
