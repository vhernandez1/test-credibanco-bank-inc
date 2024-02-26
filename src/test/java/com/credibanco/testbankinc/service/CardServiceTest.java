package com.credibanco.testbankinc.service;

import com.credibanco.testbankinc.dto.CardBalanceRequest;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.Card;
import com.credibanco.testbankinc.model.CardSateEnum;
import com.credibanco.testbankinc.model.Product;
import com.credibanco.testbankinc.model.Transaction;
import com.credibanco.testbankinc.repository.CardRepository;
import com.credibanco.testbankinc.repository.ProductRepository;
import com.credibanco.testbankinc.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class CardServiceTest {

    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private CardRepository mockCardRepository;
    @Mock
    private TransactionRepository mockTransactionRepository;

    private CardService cardServiceUnderTest;

    @BeforeEach
    void setUp() {
        cardServiceUnderTest = new CardService(mockProductRepository, mockCardRepository, mockTransactionRepository);
    }

    @Test
    void testCreateCard() {
        // Setup
        // Configure ProductRepository.findById(...).
        final Product product = new Product();
        product.setId(UUID.fromString("63cececb-9e70-4f93-8819-7e8bdc44fa3e"));
        product.setNumber("number");
        product.setName("name");
        final Optional<Product> optionalProduct = Optional.of(product);
        when(mockProductRepository.findById(UUID.fromString("dc11dccc-ba1b-4a79-9b1c-bdd807b4e964")))
                .thenReturn(optionalProduct);

        // Run the test
        final Card result = cardServiceUnderTest.createCard(UUID.fromString("dc11dccc-ba1b-4a79-9b1c-bdd807b4e964"));

        // Verify the results
        verify(mockCardRepository).save(any(Card.class));
    }

    @Test
    void testCreateCard_ProductRepositoryReturnsAbsent() {
        // Setup
        when(mockProductRepository.findById(UUID.fromString("dc11dccc-ba1b-4a79-9b1c-bdd807b4e964")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.createCard(
                UUID.fromString("dc11dccc-ba1b-4a79-9b1c-bdd807b4e964"))).isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testCardEnroll() {
        // Setup
        // Configure CardRepository.findByNumber(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("9777034058");
        card.setExpirationDate(new GregorianCalendar(2027, Calendar.JANUARY, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("0.00"));
        final Optional<Card> optionalCard = Optional.of(card);
            when(mockCardRepository.findByNumber("9777034058")).thenReturn(optionalCard);

        // Run the test
        final String result = cardServiceUnderTest.cardEnroll("4555179777034058");

        // Verify the results
        assertThat(result).isEqualTo("The card has been activated ");
        verify(mockCardRepository).save(any(Card.class));
    }


    @Test
    void testCardEnroll_numberCardLength() {
        // Setup
        // Configure CardRepository.findByNumber(...).
        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.cardEnroll("45551797770340589"))
                .isInstanceOf(CardRuntimeException.class);
    }




    @Test
    void testCardEnroll_CardRepositoryFindByNumberReturnsAbsent() {
        // Setup
        when(mockCardRepository.findByNumber("9777034058")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.cardEnroll("4555179777034058")).isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testDeleteCard() {
        // Setup
        // Configure CardRepository.findById(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("number");
        card.setExpirationDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("0.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findById(UUID.fromString("8764b374-1de3-41f0-91f1-48bd54516d7b")))
                .thenReturn(optionalCard);

        // Run the test
        final String result = cardServiceUnderTest.deleteCard(UUID.fromString("8764b374-1de3-41f0-91f1-48bd54516d7b"));

        // Verify the results
        assertThat(result).isEqualTo("The card has been blocked");
        verify(mockCardRepository).save(any(Card.class));
    }

    @Test
    void testDeleteCard_CardRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockCardRepository.findById(UUID.fromString("8764b374-1de3-41f0-91f1-48bd54516d7b")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.deleteCard(
                UUID.fromString("8764b374-1de3-41f0-91f1-48bd54516d7b"))).isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testBalance() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709");
        cardBalanceRequest.setBalance("1000");

        // Configure CardRepository.findByNumber(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("3281523709");
        card.setExpirationDate(new GregorianCalendar(2027, Calendar.FEBRUARY, 26).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("0.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findByNumber("3281523709")).thenReturn(optionalCard);

        // Run the test
        final String result = cardServiceUnderTest.balance(cardBalanceRequest);

        // Verify the results
        assertThat(result).isEqualTo("The card has been charged with the balance $ 1000");
        verify(mockTransactionRepository).save(any(Transaction.class));
        verify(mockCardRepository).save(any(Card.class));
    }

    @Test
    void testBalance_BalanceNegative() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709345345");
        cardBalanceRequest.setBalance("-1000");

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.balance(cardBalanceRequest))
                .isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testBalance_LengthNumberCardInvalid() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709345345999");
        cardBalanceRequest.setBalance("1000");

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.balance(cardBalanceRequest))
                .isInstanceOf(CardRuntimeException.class);
    }

    @Test
    void testBalance_CardRepositoryFindByNumberReturnsAbsent() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709");
        cardBalanceRequest.setBalance("1000");

        when(mockCardRepository.findByNumber("3281523709")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.balance(cardBalanceRequest))
                .isInstanceOf(CardRuntimeException.class);
    }


    @Test
    void testBalance_expiredCard() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709");
        cardBalanceRequest.setBalance("1000");

        // Configure CardRepository.findByNumber(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("3281523709");
        card.setExpirationDate(new GregorianCalendar(2020, Calendar.FEBRUARY, 26).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("0.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findByNumber("3281523709")).thenReturn(optionalCard);

        // Run the test
        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.balance(cardBalanceRequest))
                .isInstanceOf(CardRuntimeException.class);

    }


    @Test
    void testBalance_stateCardDifferentActive() {
        // Setup
        final CardBalanceRequest cardBalanceRequest = new CardBalanceRequest();
        cardBalanceRequest.setCardId("4555173281523709");
        cardBalanceRequest.setBalance("1000");

        // Configure CardRepository.findByNumber(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("3281523709");
        card.setExpirationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 27).getTime());
        card.setState(CardSateEnum.block);
        card.setBalance(new BigDecimal("0.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findByNumber("3281523709")).thenReturn(optionalCard);

        // Run the test
        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.balance(cardBalanceRequest))
                .isInstanceOf(CardRuntimeException.class);

    }

    @Test
    void testGetBalance() {
        // Setup
        // Configure CardRepository.findById(...).
        final Card card = new Card();
        card.setId(UUID.fromString("ae6958d5-f987-438f-8027-6d67b57c0adb"));
        final Product product = new Product();
        card.setProduct(product);
        card.setNumber("3281523709");
        card.setExpirationDate(new GregorianCalendar(2027, Calendar.JANUARY, 1).getTime());
        card.setState(CardSateEnum.active);
        card.setBalance(new BigDecimal("100.00"));
        final Optional<Card> optionalCard = Optional.of(card);
        when(mockCardRepository.findById(UUID.fromString("dc25c3ad-333c-4692-965e-31eff6ef65a1")))
                .thenReturn(optionalCard);

        // Run the test
        final String result = cardServiceUnderTest.getBalance(UUID.fromString("dc25c3ad-333c-4692-965e-31eff6ef65a1"));

        // Verify the results
        assertThat(result).isEqualTo("\"balance\": \"$100.00\"");
    }

    @Test
    void testGetBalance_CardRepositoryReturnsAbsent() {
        // Setup
        when(mockCardRepository.findById(UUID.fromString("dc25c3ad-333c-4692-965e-31eff6ef65a1")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> cardServiceUnderTest.getBalance(
                UUID.fromString("dc25c3ad-333c-4692-965e-31eff6ef65a1"))).isInstanceOf(CardRuntimeException.class);
    }
}
