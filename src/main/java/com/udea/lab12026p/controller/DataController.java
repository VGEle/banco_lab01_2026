package com.udea.lab12026p.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class DataController {

    private static final int NATIONS_COUNT = 10;
    private static final int CURRENCIES_COUNT = 20;
    private static final int AVIATIONS_COUNT = 20;

    private final ObjectMapper objectMapper;
    private final Faker faker;

    public DataController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.faker = new Faker(Locale.US);
    }

    @GetMapping("/")
    public String healthCheck() {
        return "HEALTH CHECK OK!";
    }

    @GetMapping("/version")
    public String version() {
        return "The actual version is 1.0.0";
    }

    @GetMapping("/nations")
    public JsonNode getRandomNations() {
        var nations = objectMapper.createArrayNode();
        for (int i = 0; i < NATIONS_COUNT; i++) {
            var nation = faker.nation();
            nations.add(objectMapper.createObjectNode()
                    .put("nationality", nation.nationality())
                    .put("capitalCity", nation.capitalCity())
                    .put("flag", nation.flag())
                    .put("language", nation.language()));
        }
        return nations;
    }

    @GetMapping("/currencies")
    public JsonNode getRandomCurrencies() {
        var currencies = objectMapper.createArrayNode();
        for (int i = 0; i < CURRENCIES_COUNT; i++) {
            var currency = faker.currency();
            currencies.add(objectMapper.createObjectNode()
                    .put("name", currency.name())
                    .put("code", currency.code()));
        }
        return currencies;
    }

    @GetMapping("/aviation")
    public JsonNode getRandomAviation() {
        var aviations = objectMapper.createArrayNode();
        for (int i = 0; i < AVIATIONS_COUNT; i++) {
            var aviation = faker.aviation();
            aviations.add(objectMapper.createObjectNode()
                    .put("aircraft", aviation.aircraft())
                    .put("airport", aviation.airport())
                    .put("metar", aviation.METAR()));
        }
        return aviations;
    }
}
