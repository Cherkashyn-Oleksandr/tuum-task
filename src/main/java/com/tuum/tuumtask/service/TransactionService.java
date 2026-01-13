package com.tuum.tuumtask.service;

import com.tuum.tuumtask.dto.AccountEvent;
import com.tuum.tuumtask.dto.CreateTransactionRequest;
import com.tuum.tuumtask.dto.TransactionResponse;
import com.tuum.tuumtask.dto.TransactionListResponse;
import com.tuum.tuumtask.exception.BusinessException;
import com.tuum.tuumtask.exception.ErrorCode;
import com.tuum.tuumtask.mapper.AccountMapper;
import com.tuum.tuumtask.mapper.BalanceMapper;
import com.tuum.tuumtask.mapper.TransactionMapper;
import com.tuum.tuumtask.model.Currency;
import com.tuum.tuumtask.model.Direction;
import com.tuum.tuumtask.model.Balance;
import com.tuum.tuumtask.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;
    private final PublisherService PublisherService;
    private final TransactionalPublisherService TransactionalPublisherService;

    public TransactionService(
            AccountMapper accountMapper,
            BalanceMapper balanceMapper,
            TransactionMapper transactionMapper,
            PublisherService publisherService,
            TransactionalPublisherService transactionalpublisherService
    ) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
        PublisherService = publisherService;
        this.TransactionalPublisherService = transactionalpublisherService;
    }



    @Transactional
    public TransactionResponse createTransaction(
            UUID accountId,
            CreateTransactionRequest request
    ) {

        if (accountMapper.findById(accountId) == null) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY);
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BusinessException(ErrorCode.DESCRIPTION_MISSING);
        }

        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }

        Direction direction;
        try {
            direction = Direction.valueOf(request.getDirection());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_DIRECTION);
        }

        Currency currency;
        try {
            currency = Currency.valueOf(request.getCurrency());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY);
        }

        Balance balance = balanceMapper.findByAccountIdAndCurrency(
                accountId, currency.name()
        );

        if (balance == null) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY);
        }

        BigDecimal newAmount = balance.getAmount();

        if (direction == Direction.IN) {
            newAmount = newAmount.add(request.getAmount());
        } else {
            if (newAmount.compareTo(request.getAmount()) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_FUNDS);
            }
            newAmount = newAmount.subtract(request.getAmount());
        }

        balance.setAmount(newAmount);
        balanceMapper.update(balance);

        TransactionalPublisherService.publishAfterCommit(() ->
                PublisherService.publish(
                        "balance.updated",
                        new AccountEvent("BALANCE_UPDATED", accountId, balance)
                )
        );

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountId(accountId);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDirection(direction.name());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(Instant.now());

        transactionMapper.insert(transaction);

        TransactionalPublisherService.publishAfterCommit(() ->
                PublisherService.publish(
                        "transaction.created",
                        new AccountEvent("TRANSACTION_CREATED", accountId, transaction)
                )
        );

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



