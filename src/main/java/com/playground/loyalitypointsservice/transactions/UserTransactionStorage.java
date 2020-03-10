package com.playground.loyalitypointsservice.transactions;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserTransactionStorage {
    void saveTransaction(UUID userId, UUID transactionId, long tempPoints, LocalDate date);

    long getPendingPointsFromDate(UUID userId, LocalDate date);

    List<UserPointTransaction> getTransactions(UUID userId);
}
