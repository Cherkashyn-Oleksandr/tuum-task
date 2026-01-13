package com.tuum.tuumtask.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.tuumtask.BaseIntegrationTest;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerIT extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createAccount_success() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setCountry("EE");
        request.setCurrencies(List.of("EUR", "USD"));

        mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.balances.length()").value(2))
                .andExpect(jsonPath("$.balances[0].currency").exists());
    }

    @Test
    void createAccount_invalidCurrency() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setCountry("EE");
        request.setCurrencies(List.of("EUR", "BTC"));

        mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_CURRENCY"));
    }

    @Test
    void getAccount_notFound() throws Exception {
        mockMvc.perform(get("/accounts/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ACCOUNT_NOT_FOUND"));
    }

}
