package com.playground.loyalitypointsservice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class LoyaltyPointsScheduler {
    private UserLoyaltyService userLoyaltyService;

    LoyaltyPointsScheduler(UserLoyaltyService userLoyaltyService) {

        this.userLoyaltyService = userLoyaltyService;
    }

    @Scheduled(cron = "17 17 23 ? * SUN")
    public void cronJobSch() {
        Set<UUID> usersIds = userLoyaltyService.getUsersIds();

        for (UUID uuid : usersIds) {
            userLoyaltyService.reevaluatePointsForUser(uuid);
        }
    }
}
