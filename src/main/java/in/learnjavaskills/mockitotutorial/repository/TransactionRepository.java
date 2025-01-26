package in.learnjavaskills.mockitotutorial.repository;

import in.learnjavaskills.mockitotutorial.dto.TransactionDetail;

import java.math.BigDecimal;

public class TransactionRepository
{
    public boolean isUniqueTransactionId(long transactionId)
    {
        return false;
    }

    public void saveTransaction(TransactionDetail transactionDetail)
    {
        System.out.println("transaction saved");
    }

    public void saveNetBankingTransaction(String username, BigDecimal amount)
    {
        System.out.println("transaction saved for username " + username + " with amount " + amount);
    }
}
