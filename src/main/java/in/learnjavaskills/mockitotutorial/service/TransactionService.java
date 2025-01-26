package in.learnjavaskills.mockitotutorial.service;

import in.learnjavaskills.mockitotutorial.exception.TransactionException;
import in.learnjavaskills.mockitotutorial.dto.TransactionDetail;
import in.learnjavaskills.mockitotutorial.dto.TransactionStatus;
import in.learnjavaskills.mockitotutorial.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TransactionService
{
    private TransactionRepository transactionRepository;

    private NotificationService notificationService;
    public TransactionService(TransactionRepository transactionRepository, NotificationService notificationService)
    {
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    private int reTryAttempt = 0;

    public TransactionStatus creditCardTransaction(TransactionDetail transactionDetail)
            throws TransactionException
    {
        if (Objects.isNull(transactionDetail))
            throw new TransactionException("transactionDetails must be non null");

        System.out.println(transactionDetail.toString());

        boolean uniqueTransactionId = transactionRepository.isUniqueTransactionId(transactionDetail.transactionId());
        System.out.println("is uniqueTransactionId : " + uniqueTransactionId);
        if (!uniqueTransactionId)
            throw new TransactionException("transaction id must be unique");

        boolean isTransactionSuccess = completeTransaction(transactionDetail);
        if (isTransactionSuccess)
            return new TransactionStatus((short) 200, "success");
        return new TransactionStatus((short) 501, "fail");
    }

    private boolean completeTransaction(TransactionDetail transactionDetail)
    {
        if (transactionDetail.cardNumber() < 0)
            return false;

        transactionRepository.saveTransaction(transactionDetail);
        return true;
    }

    public void netBankingTransaction(String username, String password,
            BigDecimal amount, Long transferAccountNumber) throws InterruptedException {
        boolean isValidUser = authenticateUser(username, password);
        if (isValidUser) {
            boolean isTransferSuccess = transferAmount(transferAccountNumber, username, amount);
            if (isTransferSuccess) {
                // Send notification to user
                try {
                    boolean isNotificationSend = notificationService.sendNotification("toEmail@address.com",
                            "Transaction success for " + amount, "Hi " + username + " \n Successfully transfer amount " + amount);
                } catch (Exception exception) {
                    System.out.println("first exception");
                    TimeUnit.SECONDS.sleep(10L);
                    // Re-Trying sending notification
                    try
                    {
                        boolean isNotificationSend = notificationService.sendNotification("toEmail@address.com", "Transaction success for " + amount, "Hi " + username + " \n Successfully transfer amount " + amount);
                    } catch (Exception exception1)
                    {
                        System.out.println("second exception occur");
                        boolean isNotificationSend = notificationService.sendNotification("toEmail@address.com", "Transaction success for " + amount, "Hi " + username + " \n Successfully transfer amount " + amount);
                    }
                }

                // save net banking transaction
                try {
                    transactionRepository.saveNetBankingTransaction(username, amount);
                } catch (Exception exception) {
                    TimeUnit.SECONDS.sleep(10L);
                    // Re-Trying to save net banking transaction
                    transactionRepository.saveNetBankingTransaction(username, amount);
                }
            }
        }
    }

    private boolean authenticateUser(String username, String password) {
        if (Objects.nonNull(username) && Objects.nonNull(password))
            return true;
        return false;
    }

    private boolean transferAmount(Long transferAccountNumber, String fromUserAccount, BigDecimal amount) {
        if (transferAccountNumber > 0 && Objects.nonNull(fromUserAccount))
            return true;
        return false;
    }

}
