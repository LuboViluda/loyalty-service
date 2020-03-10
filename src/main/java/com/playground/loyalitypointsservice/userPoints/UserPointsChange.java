package com.playground.loyalitypointsservice.userPoints;

import java.util.UUID;

public final class UserPointsChange {
    private final UUID uuid;
    private final long change;

    public UserPointsChange(UUID uuid, long change) {
        this.uuid = uuid;
        this.change = change;
    }

    public long getChange() {
        return change;
    }

    public UUID getUserUuid() {
        return uuid;
    }
}
