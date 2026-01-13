package com.tuum.tuumtask.dto;

import java.math.BigDecimal;
import java.util.UUID;

//data from APi after completed transaction
public class TransactionResponse {

    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private String direction;
    private BigDecimal balanceAfter;


    //getters setters
    public UUID getAccountId() {
        return accountId;
    }

    public void  setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
