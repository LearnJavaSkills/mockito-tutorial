package in.learnjavaskills.mockitotutorial.dto;

import java.math.BigDecimal;

public record TransactionDetail(long transactionId, long cardNumber, byte cardExpiryYear,
                                byte cardExpiryMonth, BigDecimal transactionAmount)
{}
