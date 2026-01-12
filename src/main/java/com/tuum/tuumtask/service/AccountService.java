package com.tuum.tuumtask.service;

import com.tuum.tuumtask.dto.AccountEvent;
import com.tuum.tuumtask.dto.AccountResponse;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import com.tuum.tuumtask.exception.InvalidCurrencyException;
import com.tuum.tuumtask.mapper.AccountMapper;
import com.tuum.tuumtask.mapper.BalanceMapper;
import com.tuum.tuumtask.mapper.TransactionMapper;
import com.tuum.tuumtask.model.Account;
import com.tuum.tuumtask.model.Balance;
import com.tuum.tuumtask.model.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;
    private final PublisherService PublisherService;
    private final TransactionalPublisherService TransactionalPublisherService;

    public AccountService(
            AccountMapper accountMapper,
            BalanceMapper balanceMapper,
            TransactionMapper transactionMapper,
            PublisherService publisherService,
            TransactionalPublisherService transactionalPublisherService

    ) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
        this.PublisherService = publisherService;
        this.TransactionalPublisherService = transactionalPublisherService;

    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        request.getCurrencies().forEach(currency -> {
            try {
                Currency.valueOf(currency);
            } catch (IllegalArgumentException e) {
                throw new InvalidCurrencyException("Invalid currency: " + currency);
            }
        });

        UUID accountId = UUID.randomUUID();

        Account account = new Account();
        account.setId(accountId);
        account.setCustomerId(request.getCustomerId());
        account.setCountry(request.getCountry());
        account.setCreatedAt(Instant.now());

        accountMapper.insert(account);

        TransactionalPublisherService.publishAfterCommit(() ->
                PublisherService.publish(
                        "account.created",
                        new AccountEvent("ACCOUNT_CREATED", accountId, account)
                )
        );

        for (String currency : request.getCurrencies()) {
            Balance balance = new Balance();
            balance.setId(UUID.randomUUID());
            balance.setAccountId(accountId);
            balance.setCurrency(currency);
            balance.setAmount(BigDecimal.ZERO);

            balanceMapper.insert(balance);

            TransactionalPublisherService.publishAfterCommit(() ->
                    PublisherService.publish(
                            "balance.created",
                            new AccountEvent("BALANCE_CREATED", accountId, balance)
                    )
            );
        }

        return getAccount(accountId);
    }

    public AccountResponse getAccount(UUID accountId) {

        Account account = accountMapper.findById(accountId);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        List<Balance> balances = balanceMapper.findByAccountId(accountId);

        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setBalances(
                balances.stream().map(b -> {
                    AccountResponse.BalanceResponse br =
                            new AccountResponse.BalanceResponse();
                    br.setCurrency(b.getCurrency());
                    br.setAvailableAmount(b.getAmount());
                    return br;
                }).toList()
        );

        return response;
    }

}
