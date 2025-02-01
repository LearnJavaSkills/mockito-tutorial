package in.learnjavaskills.mockitotutorial.service;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    public boolean isActive(Long accountNumber) {
        return Objects.nonNull(accountNumber) && accountNumber > 1;
    }

    public boolean isUpiAllowed(Long accountNumber, BigDecimal amount) {
        System.out.println("Checking is UPI transaction active for account " + accountNumber + " for amount " + amount);
        return Objects.nonNull(accountNumber) && accountNumber > 1
                && Objects.nonNull(amount) && amount.compareTo(BigDecimal.valueOf(5000d)) < 0;
    }
}
