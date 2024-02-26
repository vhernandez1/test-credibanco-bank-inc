package com.credibanco.testbankinc.service;

import com.credibanco.testbankinc.dto.GetTransactionResponse;
import com.credibanco.testbankinc.dto.PurchaseRequest;
import com.credibanco.testbankinc.dto.ReverseTransactionRequest;
import com.credibanco.testbankinc.dto.TransactionResponse;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.*;
import com.credibanco.testbankinc.repository.CardRepository;
import com.credibanco.testbankinc.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository mockCardRepository;
    @Mock
    private TransactionRepository mockTransactionRepository;

    private TransactionService transactionServiceUnderTest;

    @BeforeEach
    void setUp() {
        transactionServiceUnderTest = new TransactionService(mockCardRepository, mockTransactionRepository);
    }

    @Test
    void testPurchase() {
        // Setup
        final PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setCardId("4555173281523709");
        purchaseRequest.setPrice("100");

        final TransactionResponse expectedResult = new TransactionResponse();
        expectedResult.setTransactionId(UUID.fromString("83406b7e-9682-4e69-b3d3-0753dd4a6431"));
        expectedResult.setMessage("The purchase is completed successful for $ 100");

        // Configure CardRepository.findByNumber(...).
        final Card card = new Card();
        card.setId(UUID.fromString("14c8fb9d-a3ce-471a-bb36-978a46df250b"));
        final Product product = new Product();
        card.setProduct(product);
        card.setExpirationDate(new GregorianCalendar(2027, Calendar.JANUARY, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("1000.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findByNumber("3281523709")).thenReturn(optionalCard);

        // Run the test
        final TransactionResponse result = transactionServiceUnderTest.purchase(purchaseRequest);

        // Verify the results
        assertThat(result.getMessage()).isEqualTo(expectedResult.getMessage());
        verify(mockTransactionRepository).save(any(Transaction.class));
        verify(mockCardRepository).save(any(Card.class));
    }

    @Test
    void testPurchase_CardRepositoryFindByNumberReturnsAbsent() {
        // Setup
        final PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setCardId("4555173281523709");
        purchaseRequest.setPrice("100");

        when(mockCardRepository.findByNumber("3281523709")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> transactionServiceUnderTest.purchase(purchaseRequest))
                .isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testGetTransaction() {
        // Setup
        final GetTransactionResponse expectedResult = new GetTransactionResponse(TransactionStateEnum.completed.name(),
                Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0)), TransactionTypeEnum.debit.name(), new BigDecimal("100.00"));

        // Configure TransactionRepository.findById(...).
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.fromString("3bdf455f-385f-4b81-a257-b0c83f615d5d"));
        final Card card = new Card();
        card.setExpirationDate(new GregorianCalendar(2024, Calendar.MARCH, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("900.00"));
        transaction.setCard(card);
        transaction.setState(TransactionStateEnum.completed);
        transaction.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0)));
        transaction.setType(TransactionTypeEnum.debit);
        transaction.setValue(new BigDecimal("100.00"));
        final Optional<Transaction> transactionOptional = Optional.of(transaction);
        when(mockTransactionRepository.findById(UUID.fromString("d1a07602-8764-4b9e-92ac-f4243e8e27c1")))
                .thenReturn(transactionOptional);

        // Run the test
        final GetTransactionResponse result = transactionServiceUnderTest.getTransaction(
                UUID.fromString("d1a07602-8764-4b9e-92ac-f4243e8e27c1"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetTransaction_TransactionRepositoryReturnsAbsent() {
        // Setup
        when(mockTransactionRepository.findById(UUID.fromString("d1a07602-8764-4b9e-92ac-f4243e8e27c1")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> transactionServiceUnderTest.getTransaction(
                UUID.fromString("d1a07602-8764-4b9e-92ac-f4243e8e27c1"))).isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testReverse() {
        // Setup
        final ReverseTransactionRequest reverseTransactionRequest = new ReverseTransactionRequest();
        reverseTransactionRequest.setCardId("4555173281523709");
        reverseTransactionRequest.setTransactionId(UUID.fromString("5887565a-5b43-4b56-8510-0536757f3c2f"));

        final TransactionResponse expectedResult = new TransactionResponse();
        expectedResult.setTransactionId(UUID.fromString("3bdf455f-385f-4b81-a257-b0c83f615d5d"));
        expectedResult.setMessage("The reverse is completed successful for $ 100.00");

        // Configure TransactionRepository.findByIdAndCardNumber(...).
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.fromString("3bdf455f-385f-4b81-a257-b0c83f615d5d"));
        final Card card = new Card();
        card.setExpirationDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("100.00"));
        transaction.setCard(card);
        transaction.setState(TransactionStateEnum.completed);
        transaction.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)));
        transaction.setUpdateDate(Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)));
        transaction.setType(TransactionTypeEnum.debit);
        transaction.setValue(new BigDecimal("100.00"));
        final Optional<Transaction> transactionOptional = Optional.of(transaction);
        when(mockTransactionRepository.findByIdAndCardNumber(UUID.fromString("5887565a-5b43-4b56-8510-0536757f3c2f"),
                "3281523709")).thenReturn(transactionOptional);

        // Run the test
        final TransactionResponse result = transactionServiceUnderTest.reverse(reverseTransactionRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockTransactionRepository).save(any(Transaction.class));
        verify(mockCardRepository).save(any(Card.class));
    }

    @Test
    void testReverse_TransactionRepositoryFindByIdAndCardNumberReturnsAbsent() {
        // Setup
        final ReverseTransactionRequest reverseTransactionRequest = new ReverseTransactionRequest();
        reverseTransactionRequest.setCardId("4555173281523709");
        reverseTransactionRequest.setTransactionId(UUID.fromString("5887565a-5b43-4b56-8510-0536757f3c2f"));

        when(mockTransactionRepository.findByIdAndCardNumber(UUID.fromString("5887565a-5b43-4b56-8510-0536757f3c2f"),
                "3281523709")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> transactionServiceUnderTest.reverse(reverseTransactionRequest))
                .isInstanceOf(CardRuntimeException.class);
    }
}
