package com.tuum.tuumtask.service;

import com.tuum.tuumtask.dto.AccountResponse;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import com.tuum.tuumtask.dto.CreateTransactionRequest;
import com.tuum.tuumtask.dto.TransactionResponse;
import com.tuum.tuumtask.exception.InvalidCurrencyException;
import com.tuum.tuumtask.mapper.AccountMapper;
import com.tuum.tuumtask.mapper.BalanceMapper;
import com.tuum.tuumtask.mapper.TransactionMapper;
import com.tuum.tuumtask.model.Direction;
import com.tuum.tuumtask.model.Account;
import com.tuum.tuumtask.model.Balance;
import com.tuum.tuumtask.model.Currency;
import com.tuum.tuumtask.model.Transaction;
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

    public AccountService(
            AccountMapper accountMapper,
            BalanceMapper balanceMapper,
            TransactionMapper transactionMapper
    ) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        // validate currencies
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

        for (String currency : request.getCurrencies()) {
            Balance balance = new Balance();
            balance.setId(UUID.randomUUID());
            balance.setAccountId(accountId);
            balance.setCurrency(currency);
            balance.setAmount(BigDecimal.ZERO);

            balanceMapper.insert(balance);
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

    @Transactional
    public TransactionResponse createTransaction(
            UUID accountId,
            CreateTransactionRequest request
    ) {
        Direction direction = Direction.valueOf(request.getDirection());

        Balance balance = balanceMapper
                .findByAccountIdAndCurrency(accountId, request.getCurrency())
                .orElseThrow(() ->
                        new RuntimeException("Balance not found for currency"));

        if (direction == Direction.OUT &&
                balance.getAmount().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        BigDecimal newAmount =
                direction == Direction.IN
                        ? balance.getAmount().add(request.getAmount())
                        : balance.getAmount().subtract(request.getAmount());

        balance.setAmount(newAmount);
        balanceMapper.update(balance);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountId(accountId);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDirection(direction.name());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(Instant.now());

        transactionMapper.insert(transaction);

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getId());
        response.setAccountId(transaction.getAccountId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDirection(transaction.getDirection());
        response.setBalanceAfter(newAmount);

        return response;
    }

}
