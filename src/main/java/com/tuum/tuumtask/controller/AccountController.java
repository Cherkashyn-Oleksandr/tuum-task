package com.tuum.tuumtask.controller;

import com.tuum.tuumtask.dto.AccountResponse;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import com.tuum.tuumtask.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    //Create new Account
    public AccountResponse createAccount(
            @RequestBody CreateAccountRequest request
    ) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountId}")
    //Get account data by ID
    public AccountResponse getAccount(
            @PathVariable UUID accountId
    ) {
        return accountService.getAccount(accountId);
    }

}
