package com.tuum.tuumtask.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionalPublisherService {

    private final PublisherService publisher;

    public TransactionalPublisherService(PublisherService publisher) {
        this.publisher = publisher;
    }

    public void publishAfterCommit(Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        action.run();
                    }
                }
        );
    }
}
