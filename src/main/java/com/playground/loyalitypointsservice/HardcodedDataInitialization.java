package com.playground.loyalitypointsservice;

import com.playground.loyalitypointsservice.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class HardcodedDataInitialization implements ApplicationRunner {
    private static final String TRANSACTION_ID_1 = "0000-00-00-00-000101";
    private static final UUID USER_UUID_1 = UUID.fromString("0000-00-00-00-000001");

    private UserLoyaltyService userLoyaltyService;

    @Autowired
    public HardcodedDataInitialization(UserLoyaltyService userLoyaltyService) {
        this.userLoyaltyService = userLoyaltyService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDate lastSundayDate = DateUtility.getLastSundayDate();
        // generate available points
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate);
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(1));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(2));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(3));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(4));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(5));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.minusDays(6));
        DateUtility.fixDate(lastSundayDate.minusDays(7));
        userLoyaltyService.reevaluatePointsForUser(USER_UUID_1);
        DateUtility.resetToDefaultDate();

        // generate pending points
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.plusDays(2));
        userLoyaltyService.addNewTransactionEvent(USER_UUID_1, UUID.randomUUID(), 1000, lastSundayDate.plusDays(1));
    }
}
