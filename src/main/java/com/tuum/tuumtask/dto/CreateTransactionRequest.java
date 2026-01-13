package com.tuum.tuumtask.dto;

import java.math.BigDecimal;

//data for creation transaction
public class CreateTransactionRequest {

    private BigDecimal amount;
    private String currency;
    private String direction;
    private String description;


    //getters setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
