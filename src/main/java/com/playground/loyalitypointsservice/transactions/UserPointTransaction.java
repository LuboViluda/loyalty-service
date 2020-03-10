package com.playground.loyalitypointsservice.transactions;

import java.time.LocalDate;
import java.util.UUID;

public class UserPointTransaction {
    private final UUID userId;
    private final UUID transactionId;
    private final long tempPoints;
    private final LocalDate date;

    public UserPointTransaction(UUID userId, UUID transactionId, long tempPoints, LocalDate date) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.tempPoints = tempPoints;
        this.date = date;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public long getTempPoints() {
        return tempPoints;
    }

    public LocalDate getDate() {
        return date;
    }
}
