package com.playground.loyalitypointsservice;

import com.playground.loyalitypointsservice.transactions.SimpleInMemoryUserTransactionStorage;
import com.playground.loyalitypointsservice.transactions.UserTransactionStorage;
import com.playground.loyalitypointsservice.userPoints.LoyaltyPointsStorage;
import com.playground.loyalitypointsservice.utils.DateUtility;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserLoyaltyServiceTest {
    private final static UUID USER_ID = UUID.fromString("0000-00-00-00-000000");
    private final static UUID TRANSACTION_ID = UUID.fromString("0000-00-00-00-000001");
    private final static LocalDate DATE = LocalDate.of(2020, 1, 1);
    private final static int INSUFFICIENT_COUNT = 493;
    private final static int SUFFICIENT_COUNT = 500;

    private LoyaltyPointsStorage loyaltyPointsStorage;
    private UserTransactionStorage userTransactionStorage;
    private UserLoyaltyService userLoyaltyService;

    @BeforeMethod
    void beforeMethod() {
        userTransactionStorage = mock(UserTransactionStorage.class);
        loyaltyPointsStorage = mock(LoyaltyPointsStorage.class);
        userLoyaltyService = new UserLoyaltyService(userTransactionStorage, loyaltyPointsStorage);
    }

    @AfterMethod
    void afterMethod() {
        DateUtility.resetToDefaultDate();
    }

    @DataProvider(name = "transactionEventsDataProvider")
    public static Object[][] transactionEventsDataProvider() {
        return new Object[][]{
                {"zero transaction", 0, 0},
                {"small transaction", 100, 100},
                {"small transaction upper bound", 5000, 5000},
                {"medium transaction lower bound", 5001, 5002},
                {"medium transaction upper bound", 7500, 10000},
                {"large transaction lower bound", 7501, 10003},
                {"large transaction", 7800, 10900},
        };
    }

    @Test(dataProvider = "transactionEventsDataProvider")
    void addNewTransactionEvent_pointsCountingLogic(String description, long transactionAmount, long expectedPointsAmount) {
        userLoyaltyService.addNewTransactionEvent(USER_ID, TRANSACTION_ID, transactionAmount, DATE);

        verify(userTransactionStorage).saveTransaction(USER_ID, TRANSACTION_ID, expectedPointsAmount, DATE);
    }

    @Test
    public void reevaluateForUser_pointsAdded() {
        prepareTransactionsAllDays(SUFFICIENT_COUNT);

        userLoyaltyService.reevaluatePointsForUser(USER_ID);

        verify(loyaltyPointsStorage).addPointsToUser(USER_ID, 500);
    }

    @Test
    public void reevaluateForUser_pointsNotAdded_notEnoughPoints() {
        prepareTransactionsAllDays(INSUFFICIENT_COUNT);

        userLoyaltyService.reevaluatePointsForUser(USER_ID);

        verify(loyaltyPointsStorage, never()).addPointsToUser(any(), anyLong());
    }

    @Test
    public void reevaluateForUser_pointsNotAdded_notAllDays() {
        prepareTransactionsMissingDays();

        userLoyaltyService.reevaluatePointsForUser(USER_ID);

        verify(loyaltyPointsStorage, never()).addPointsToUser(any(), anyLong());
    }

    @Test
    public void reevaluateForUser_pointsDiscarded_noActivityLast5Weeks() {
       prepareDiscardScenario();

        userLoyaltyService.reevaluatePointsForUser(USER_ID);

        verify(loyaltyPointsStorage).usePointForUser(USER_ID, 10L);
    }

    private void prepareTransactionsAllDays(long totalCount) {
        UserTransactionStorage userTransactionStorage = new SimpleInMemoryUserTransactionStorage();
        userLoyaltyService = new UserLoyaltyService(userTransactionStorage, loyaltyPointsStorage);
        DateUtility.fixDate(LocalDate.of(2020, 1, 19));

        long pointsToLocalCount = totalCount - 6;
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 13));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 14));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 15));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 16));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 17));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, LocalDate.of(2020, 1, 18));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, pointsToLocalCount, LocalDate.of(2020, 1, 19));
    }

    private void prepareTransactionsMissingDays() {
        UserTransactionStorage userTransactionStorage = new SimpleInMemoryUserTransactionStorage();
        userLoyaltyService = new UserLoyaltyService(userTransactionStorage, loyaltyPointsStorage);
        DateUtility.fixDate(LocalDate.of(2020, 1, 19));

//      missing date 14.1
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 13));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 13));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 15));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 16));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 17));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 18));
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1000, LocalDate.of(2020, 1, 19));
    }

    private void prepareDiscardScenario() {
        doReturn(10L).when(loyaltyPointsStorage).getPointsForUser(USER_ID);
        UserTransactionStorage userTransactionStorage = new SimpleInMemoryUserTransactionStorage();
        userLoyaltyService = new UserLoyaltyService(userTransactionStorage, loyaltyPointsStorage);
        LocalDate fixedDate = LocalDate.of(2020, 1, 19);
        DateUtility.fixDate(fixedDate);
        userTransactionStorage.saveTransaction(USER_ID, TRANSACTION_ID, 1, fixedDate.minusWeeks(5));
    }
}