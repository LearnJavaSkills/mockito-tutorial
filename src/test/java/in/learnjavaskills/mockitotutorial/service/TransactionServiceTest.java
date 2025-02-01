package in.learnjavaskills.mockitotutorial.service;

import in.learnjavaskills.mockitotutorial.dto.TransactionDetail;
import in.learnjavaskills.mockitotutorial.dto.TransactionStatus;
import in.learnjavaskills.mockitotutorial.exception.TransactionException;
import in.learnjavaskills.mockitotutorial.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest
{
    @Mock
    private TransactionRepository transactionRepository;

    @Captor
    ArgumentCaptor<TransactionDetail> transactionDetailArgumentCaptor;

    @Mock private NotificationService notificationService;

    @InjectMocks
    private TransactionService transactionService;



    private long transactionId = 101L;
    private TransactionDetail transactionDetail = new TransactionDetail(transactionId,
            1234_5678_9101_1213L, (byte) 23, (byte) 11, BigDecimal.TEN);

    @Test
    void creditCardTransactionPositiveFlow()
    {
        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        TransactionStatus transactionStatus = transactionService.creditCardTransaction(transactionDetail);
        short statusCode = transactionStatus.statusCode();
        assertEquals(statusCode, 200);
    }

    @Test
    void verifyTransactionSaved()
    {
        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        transactionService.creditCardTransaction(transactionDetail);

        BDDMockito.verify(transactionRepository, Mockito.atMost(6))
                .saveTransaction(ArgumentMatchers.any());
    }

    @Test
    void verifyTransactionSavedWithCorrectValues()
    {
        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        transactionService.creditCardTransaction(transactionDetail);

        BDDMockito.verify(transactionRepository)
                .saveTransaction(transactionDetailArgumentCaptor.capture());

        TransactionDetail expectedTransactionDetail = transactionDetailArgumentCaptor.getValue();
        assertEquals(transactionDetail.transactionId(), expectedTransactionDetail.transactionId());
        assertEquals(transactionDetail.transactionAmount(), expectedTransactionDetail.transactionAmount());
    }

    @Test
    void verifyExceptionThrown()
    {
        Assertions.assertThatThrownBy(()-> { transactionService.creditCardTransaction(null); })
                .hasMessageContaining("transactionDetails must be non null")
                .isExactlyInstanceOf(TransactionException.class);
    }

    @Test
    void doNothingWhenSaveTransactionIsInvoked()
    {
        BDDMockito.doNothing()
                .when(transactionRepository)
                .saveTransaction(transactionDetail);

        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        transactionService.creditCardTransaction(transactionDetail);
    }

    @Test
    void argumentCaptorWithDoNothingOnSaveTransaction()
    {
        ArgumentCaptor<TransactionDetail> transactionDetailArgumentCaptor = ArgumentCaptor.forClass(TransactionDetail.class);

        BDDMockito.doNothing()
                .when(transactionRepository)
                .saveTransaction(transactionDetailArgumentCaptor.capture());

        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        transactionService.creditCardTransaction(transactionDetail);

        TransactionDetail transactionDetailArgumentCaptorValue = transactionDetailArgumentCaptor.getValue();
        Assertions.assertThat(transactionDetailArgumentCaptorValue).isNotNull();
        Assertions.assertThat(transactionDetailArgumentCaptorValue.transactionAmount()).isEqualTo(transactionDetail.transactionAmount());
        Assertions.assertThat(transactionDetailArgumentCaptorValue.transactionId()).isEqualTo(transactionId);
    }

    @Test
    void doThrowWhenSaveTransactionInvoke()
    {
        BDDMockito.doThrow(ClassCastException.class)
                .when(transactionRepository)
                .saveTransaction(transactionDetail);

        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        Assertions.assertThatThrownBy(()-> transactionService.creditCardTransaction(transactionDetail))
                .isExactlyInstanceOf(ClassCastException.class);
    }

    @Test
    void doCallRealMethodWhenSaveTransactionInvoke()
    {
        BDDMockito.doCallRealMethod()
                .when(transactionRepository)
                .saveTransaction(transactionDetail);

        BDDMockito.given(transactionRepository.isUniqueTransactionId(ArgumentMatchers.anyLong()))
                .willReturn(true);

        transactionService.creditCardTransaction(transactionDetail);

        BDDMockito.verify(transactionRepository, Mockito.times(1))
                .saveTransaction(transactionDetail);

    }


    // Let's write a unit test to throw exception when  notificationService.sendNotification() called

    @Test
    void testWhenExceptionThrowWhileSendingNotification() {

        // mock the notification service and throw TransactionException when sendNotification() called to test the catch block
        BDDMockito.given(notificationService.sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any()))
                .willThrow(TransactionException.class);

        // verify that the TransactionException raise when calling the netBankingTransaction.
        Assertions.assertThatThrownBy( ()-> { transactionService.netBankingTransaction("LearnJavaSkills.in",
                "password", BigDecimal.TEN, 123456789L);} )
                .isExactlyInstanceOf(TransactionException.class);

        // let's verify only 2 times notificationService.sendNotification() called
        BDDMockito.verify(notificationService, Mockito.times(2))
                .sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }


    // let's now see how we can throw exception on void method.

    @Test
    void testWhenExceptionThrownWhileSavingNetBankTransaction() {

        // mock the transactionRepository to throw exception when saveNetBankingTransaction() called
        BDDMockito.doThrow(TransactionException.class)
                .when(transactionRepository)
                .saveNetBankingTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());

        // verify TransactionException raise or not
        Assertions.assertThatThrownBy( ()-> transactionService.netBankingTransaction("LearnJavaSkills.in",
                "password", BigDecimal.TEN, 123456789L) )
                .isExactlyInstanceOf(TransactionException.class);

        // let's verify 3 times saveNetNBankingTransaction() called because in our business logic we are calling two times only.
        BDDMockito.verify(transactionRepository, Mockito.times(3))
                .saveNetBankingTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    // let's learn how to throw exception with message i.e., Exception as a object
    // we are going to throw exception on the same previous example i.e., saveNetBankingTransaction() method

    @Test
    void testWhenExceptionThrownWhileSavingNetBankingTransactionWithMessage() {

        String message = "unable to save net banking transaction";
        // mock the transaction repository to throw exception as a object
        BDDMockito.doThrow(new TransactionException(message))
                .when(transactionRepository)
                .saveNetBankingTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());

        // now let's verify exception is raise or not, this time we will also verify exception message.
        Assertions.assertThatThrownBy(()-> transactionService.netBankingTransaction("LearnJavaSkills.in",
                "password", BigDecimal.TEN, 123456789L))
                .isExactlyInstanceOf(TransactionException.class)
                .hasMessageContaining(message);
    }

    // let's now learn how to stub multiple time
    // we will throw exception two time on notificationService.sendNotification() and on the third time we will return values.
    // I'm modifying business logic to performe this activity.

    @Test
    void testWhenExceptionThrownWhileSendingNotificationStubMultipleTimes() throws InterruptedException
    {

        String message = "unable to send notification";

        // let's stub multiple times
        BDDMockito.given(notificationService.sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any()))
                .willThrow(TransactionException.class)
                .willThrow(TransactionException.class)
                .willReturn(true);

        // let's verify, 3 times notificationService.sendNotification() called

        transactionService.netBankingTransaction("LearnJavaSkills.in", "password",
                BigDecimal.TEN, 123456789L);

        BDDMockito.verify(notificationService, Mockito.times(3))
                .sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    void testWhenExceptionThrownWileSendingNotification2() throws InterruptedException
    {
        String message = "Unable to send notification to user";
        BDDMockito.given(notificationService.sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any()))
                .willThrow(new TransactionException(message));

        Assertions.assertThatThrownBy( ()-> transactionService.netBankingTransaction("LearnJavaSkills.in",
                "password",
                BigDecimal.ZERO,
                1234567890L))
                .isExactlyInstanceOf(TransactionException.class)
                .hasMessageContaining(message);

        BDDMockito.verify(notificationService, Mockito.times(3))
                .sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testWhenExceptionThrownWileSendingNotificationStubMultiCall() throws InterruptedException
    {
        String message = "Unable to send notification to user";
        BDDMockito.given(notificationService.sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any()))
                .willThrow(new TransactionException(message))
                .willReturn(true);

        transactionService.netBankingTransaction("LearnJavaSkills.in",
                        "password",
                        BigDecimal.ZERO,
                        1234567890L);

        BDDMockito.verify(notificationService, Mockito.times(2))
                .sendNotification(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testWhenExceptionThrownWhileSavingNetBankingTransaction()
    {
        // mocking transactionRepository to throw TransactionException when saveNetBankingTransaction() invoke
        String message = "unable to save net banking transaction";
        BDDMockito.doThrow(new TransactionException(message))
                .when(transactionRepository)
                .saveNetBankingTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());

        // verifying exception are thrown which mock in the above
        Assertions.assertThatThrownBy( ()-> transactionService.netBankingTransaction("LearnJavaSkills.in",
                        "password",
                        BigDecimal.ZERO,
                        1234567890L))
                .isExactlyInstanceOf(TransactionException.class)
                .hasMessageContaining(message);

        // verifying saveNetBankingTransaction method invoke two times as per the business logic
        BDDMockito.verify(transactionRepository, Mockito.times(2))
                .saveNetBankingTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Contractor Mocking
     */

    @Test
    void upiTransaction() {
        try (MockedConstruction<Account> accountMockedConstruction = Mockito.mockConstruction(Account.class, (mock, context) ->
              Mockito.when(mock.isUpiAllowed(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                      .thenReturn(true))) {
            boolean transactionStatus = transactionService.upiPayment(1234567890L, BigDecimal.TEN);
            assertTrue(transactionStatus);
        }
    }
}