package com.tuum.tuumtask.dto;

import java.util.List;
import java.util.UUID;

//data for creation account
public class CreateAccountRequest {

    private UUID customerId;
    private String country;
    private List<String> currencies;


    //getters setters
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }
}
