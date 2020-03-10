package com.playground.loyalitypointsservice.transactions;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SimpleInMemoryUserTransactionStorage implements UserTransactionStorage {
    List<UserPointTransaction> userTransactions = new ArrayList<>();

    @Override
    public void saveTransaction(UUID userId, UUID transactionId, long tempPoints, LocalDate date) {
        userTransactions.add(new UserPointTransaction(userId, transactionId, tempPoints, date));
    }

    @Override
    public long getPendingPointsFromDate(UUID userId, LocalDate from) {
        return userTransactions.stream()
                .filter(sameUserId(userId))
                .filter(t -> t.getDate().isAfter(from))
                .mapToLong(UserPointTransaction::getTempPoints)
                .sum();
    }

    @Override
    public List<UserPointTransaction> getTransactions(UUID userId) {
        return userTransactions.stream()
                .filter(sameUserId(userId))
                .collect(Collectors.toList());
    }

    private Predicate<UserPointTransaction> sameUserId(UUID userId) {
        return t -> t.getUserId().equals(userId);
    }

}
