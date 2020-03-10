package com.playground.loyalitypointsservice;

import com.playground.loyalitypointsservice.transactions.UserPointTransaction;
import com.playground.loyalitypointsservice.transactions.UserTransactionStorage;
import com.playground.loyalitypointsservice.userPoints.LoyaltyPointsStorage;
import com.playground.loyalitypointsservice.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserLoyaltyService {
    private final UserTransactionStorage userTransactionStorage;
    private final LoyaltyPointsStorage loyaltyPointsStorage;

    @Autowired
    public UserLoyaltyService(UserTransactionStorage userTransactionStorage, LoyaltyPointsStorage loyaltyPointsStorage) {
        this.userTransactionStorage = userTransactionStorage;
        this.loyaltyPointsStorage = loyaltyPointsStorage;
    }

    public void addNewTransactionEvent(UUID userId, UUID transactionId, double transactionAmount, LocalDate date) {
        long points = 0;
        long transaction = Double.valueOf(transactionAmount).longValue();
        if (transaction > 7500) {
            long above = transaction - 7500;
            points += above * 3;
            transaction -= above;
        }
        if (transaction > 5000) {
            long above = transaction - 5000;
            points += above * 2;
            transaction -= above;
        }
        points += transaction;

        userTransactionStorage.saveTransaction(userId, transactionId, points, date);
    }

    public long getUserPendingPoints(UUID userId) {
        LocalDate lastSundayDate = DateUtility.getLastSundayDate();
        return userTransactionStorage.getPendingPointsFromDate(userId, lastSundayDate);
    }

    public long getUserAvailablePoints(UUID userId) {
        return loyaltyPointsStorage.getPointsForUser(userId);
    }

    public List<Long> getUserPointHistory(UUID userId) {
        return loyaltyPointsStorage.getPointsHistoryForUser(userId);
    }

    public void usePointsForUser(UUID userId, long points) {
        loyaltyPointsStorage.usePointForUser(userId, points);
    }

    public void reevaluatePointsForUser(UUID userId) {
        List<UserPointTransaction> weekTransactions = userTransactionStorage.getTransactions(userId)
                .stream()
                .filter(t -> t.getDate().isAfter(DateUtility.getLastSundayDate()))
                .collect(Collectors.toList());

        if (weekTransactions.size() == 0) {
            controlActivityInLast5Weeks(userId);
        } else {
            addPointForLastWeek(userId, weekTransactions);
        }
    }

    private void controlActivityInLast5Weeks(UUID userId) {
        boolean wasActiveInLast5Weeks = userTransactionStorage.getTransactions(userId).stream()
                .filter(t -> t.getDate().isAfter(DateUtility.now().minusWeeks(5)))
                .findAny().isPresent();
        if (!wasActiveInLast5Weeks) {
            long allPoints = loyaltyPointsStorage.getPointsForUser(userId);
            loyaltyPointsStorage.usePointForUser(userId, allPoints);
        }
    }

    private void addPointForLastWeek(UUID userId, List<UserPointTransaction> weekTransactions) {
        long points = 0;
        Set<DayOfWeek> days = new HashSet<>();
        for (UserPointTransaction transaction : weekTransactions) {
            points += transaction.getTempPoints();
            days.add(transaction.getDate().getDayOfWeek());
        }

        if (days.size() == 7 && points >= 500) {
            loyaltyPointsStorage.addPointsToUser(userId, points);
        }
    }

    public Set<UUID> getUsersIds() {
        return loyaltyPointsStorage.getAllChanges().stream().map(t -> t.getUserUuid()).collect(Collectors.toSet());
    }
}
