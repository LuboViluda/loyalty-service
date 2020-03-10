package com.playground.loyalitypointsservice.userPoints;

import java.util.List;
import java.util.UUID;

public interface LoyaltyPointsStorage {
    void addPointsToUser(UUID userId, long points);

    void usePointForUser(UUID userId, long points);

    long getPointsForUser(UUID uuid);

    List<Long> getPointsHistoryForUser(UUID uuid);

    List<UserPointsChange> getAllChanges();
}
