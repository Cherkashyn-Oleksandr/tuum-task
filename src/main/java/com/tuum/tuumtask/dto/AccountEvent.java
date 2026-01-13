package com.tuum.tuumtask.dto;

import java.time.Instant;
import java.util.UUID;

public class AccountEvent {

    private String type;
    private UUID accountId;
    private Object payload;
    private Instant occurredAt;

    public AccountEvent() {}

    //Constructor sets type, accountId, payload, and current time
    public AccountEvent(String type, UUID accountId, Object payload) {
        this.type = type;
        this.accountId = accountId;
        this.payload = payload;
        this.occurredAt = Instant.now();
    }

    //getters
    public String getType() {
        return type;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public Object getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
