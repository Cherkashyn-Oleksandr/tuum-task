package com.tuum.tuumtask.controller;

import com.tuum.tuumtask.dto.CreateTransactionRequest;
import com.tuum.tuumtask.dto.TransactionResponse;
import com.tuum.tuumtask.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import com.tuum.tuumtask.dto.TransactionListResponse;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    //Create new transaction
    public TransactionResponse createTransaction(
            @PathVariable UUID accountId,
            @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.createTransaction(accountId, request);
    }

    @GetMapping
    //Get transactions from AccountID
    public List<TransactionListResponse> getTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return transactionService.getTransactions(accountId, limit, offset);
    }
}
