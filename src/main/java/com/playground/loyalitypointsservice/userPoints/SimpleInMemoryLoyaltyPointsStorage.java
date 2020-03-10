package com.playground.loyalitypointsservice.userPoints;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SimpleInMemoryLoyaltyPointsStorage implements LoyaltyPointsStorage {
    List<UserPointsChange> userLoyaltyPoints = new ArrayList<>();

    @Override
    public void addPointsToUser(UUID userId, long points) {
        userLoyaltyPoints.add(new UserPointsChange(userId, points));
    }

    @Override
    public void usePointForUser(UUID userId, long points) {
        long userPoints = getPointsForUser(userId);
        if (userPoints - points < 0) {
            throw new InsufficientBalanceException("Insufficient balance: for user: " + userId);
        }

        userLoyaltyPoints.add(new UserPointsChange(userId, (-points)));
    }

    @Override
    public long getPointsForUser(UUID uuid) {
        return userLoyaltyPoints.stream()
                .filter(getUserPointsChanges(uuid))
                .mapToLong(UserPointsChange::getChange)
                .sum();
    }

    @Override
    public List<Long> getPointsHistoryForUser(UUID uuid) {
        return userLoyaltyPoints.stream()
                .filter(getUserPointsChanges(uuid))
                .map(UserPointsChange::getChange)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPointsChange> getAllChanges() {
        return userLoyaltyPoints;
    }


    private Predicate<UserPointsChange> getUserPointsChanges(UUID uuid) {
        return u -> u.getUserUuid().equals(uuid);
    }
}
