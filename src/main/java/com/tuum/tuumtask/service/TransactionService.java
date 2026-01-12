package com.tuum.tuumtask.service;

import com.tuum.tuumtask.dto.AccountResponse;
import com.tuum.tuumtask.dto.CreateAccountRequest;
import com.tuum.tuumtask.dto.CreateTransactionRequest;
import com.tuum.tuumtask.dto.TransactionResponse;
import com.tuum.tuumtask.dto.TransactionListResponse;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            AccountMapper accountMapper,
            BalanceMapper balanceMapper,
            TransactionMapper transactionMapper
    ) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
    }



    @Transactional
    public TransactionResponse createTransaction(
            UUID accountId,
            CreateTransactionRequest request
    ) {

        // 1️⃣ Проверка аккаунта
        if (accountMapper.findById(accountId) == null) {
            throw new RuntimeException("Account not found");
        }

        // 2️⃣ Проверка amount
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        // 3️⃣ Direction
        Direction direction;
        try {
            direction = Direction.valueOf(request.getDirection());
        } catch (Exception e) {
            throw new RuntimeException("Invalid direction");
        }

        // 4️⃣ Баланс по валюте
        Balance balance = balanceMapper.findByAccountIdAndCurrency(
                accountId, request.getCurrency()
        );

        if (balance == null) {
            throw new RuntimeException("Invalid currency");
        }

        BigDecimal newAmount = balance.getAmount();

        // 5️⃣ IN / OUT логика
        if (direction == Direction.IN) {
            newAmount = newAmount.add(request.getAmount());
        } else {
            if (newAmount.compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient funds");
            }
            newAmount = newAmount.subtract(request.getAmount());
        }

        // 6️⃣ Обновляем баланс
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
    public List<TransactionListResponse> getTransactions(
            UUID accountId,
            int limit,
            int offset
    ) {
        List<Transaction> transactions =
                transactionMapper.findByAccountId(accountId, limit, offset);

        return transactions.stream().map(t -> {
            TransactionListResponse response = new TransactionListResponse();
            response.setTransactionId(t.getId());
            response.setAmount(t.getAmount());
            response.setCurrency(t.getCurrency());
            response.setDirection(t.getDirection());
            response.setDescription(t.getDescription());
            response.setCreatedAt(t.getCreatedAt());
            return response;
        }).toList();
    }
}



