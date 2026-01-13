package com.tuum.tuumtask.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.tuumtask.BaseIntegrationTest;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import com.tuum.tuumtask.dto.CreateTransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TransactionControllerIT extends BaseIntegrationTest{

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    private UUID createAccount() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setCountry("EE");
        request.setCurrencies(List.of("EUR"));

        String response = mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(
                objectMapper.readTree(response).get("accountId").asText()
        );
    }


    @Test
    void createTransaction_success() throws Exception {
        UUID accountId = createAccount();

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setCurrency("EUR");
        tx.setDirection("IN");
        tx.setDescription("Initial deposit");

        mockMvc.perform(post("/accounts/{id}/transactions", accountId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tx)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceAfter").value(100));
    }

    @Test
    void createTransaction_descriptionMissing() throws Exception {
        UUID accountId = createAccount();

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setAmount(BigDecimal.TEN);
        tx.setCurrency("EUR");
        tx.setDirection("IN");

        mockMvc.perform(post("/accounts/{id}/transactions", accountId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tx)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DESCRIPTION_MISSING"));
    }

    @Test
    void createTransaction_insufficientFunds() throws Exception {
        UUID accountId = createAccount();

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setAmount(BigDecimal.TEN);
        tx.setCurrency("EUR");
        tx.setDirection("OUT");
        tx.setDescription("Withdraw");

        mockMvc.perform(post("/accounts/{id}/transactions", accountId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tx)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_FUNDS"));
    }

}
