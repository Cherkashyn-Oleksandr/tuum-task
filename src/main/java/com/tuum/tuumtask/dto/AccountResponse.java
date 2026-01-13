package com.tuum.tuumtask.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

//Data for deetails returned from API
public class AccountResponse {

    private UUID accountId;
    private UUID customerId;
    private List<BalanceResponse> balances;

    public static class BalanceResponse {
        private String currency;
        private BigDecimal availableAmount;

        //getters setters
        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(BigDecimal availableAmount) {
            this.availableAmount = availableAmount;
        }
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public List<BalanceResponse> getBalances() {
        return balances;
    }

    public void setBalances(List<BalanceResponse> balances) {
        this.balances = balances;
    }
}
