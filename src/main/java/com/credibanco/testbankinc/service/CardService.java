package com.credibanco.testbankinc.service;

import com.credibanco.testbankinc.dto.CardBalanceRequest;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.*;
import com.credibanco.testbankinc.repository.CardRepository;
import com.credibanco.testbankinc.repository.ProductRepository;
import com.credibanco.testbankinc.repository.TransactionRepository;
import com.credibanco.testbankinc.util.DateUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class CardService {

    private final ProductRepository productRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;


    public CardService(ProductRepository productRepository, CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }



    public Card createCard(UUID productId) {
        String cardNumbers = generateRandomNumbers();

        Card card = new Card();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()){
            card.setProduct(optionalProduct.get());
        }else {
            throw new CardRuntimeException(String.format("The product id {%s}  has not found",productId));
        }
        card.setNumber(cardNumbers);
        card.setBalance(BigDecimal.ZERO);
        card.setState(CardSateEnum.inactive);
        card.setId(UUID.randomUUID());
        Date expirationDate = DateUtil.addYearsToDate(3,new Date());
        card.setExpirationDate(expirationDate);
        cardRepository.save(card);
        return card;
    }


    private String generateRandomNumbers() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            cardNumber.append(digit);
        }

        return cardNumber.toString();
    }

    public String cardEnroll(String cardId) {
        String cardNumber = "";
        if(cardId.length() == 16){
            cardNumber = cardId.substring(6,16);
        }else {
            throw  new CardRuntimeException("The card number have not the length valid");
        }
       Optional<Card> optionalCard = cardRepository.findByNumber(cardNumber);
        if(optionalCard.isPresent()){
            Card card = optionalCard.get();
            card.setState(CardSateEnum.active);
            cardRepository.save(card);
            return "The card has been activated ";
        }else {
            throw  new CardRuntimeException("The card number not have been found");
        }
    }

    public String deleteCard(UUID cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if(cardOptional.isPresent()){
            Card card = cardOptional.get();
            card.setState(CardSateEnum.block);
            cardRepository.save(card);
            return "The card has been blocked";

        }else {
            throw  new CardRuntimeException("The card Id not have been found");
        }
    }

    public String balance(CardBalanceRequest cardBalanceRequest) {
        BigDecimal balance;
        try {
            balance = new BigDecimal(cardBalanceRequest.getBalance());
            if(balance.compareTo(BigDecimal.ZERO) < 0){
                throw new CardRuntimeException("the balance should be positive");
            }
        }catch (NumberFormatException e){
            throw new CardRuntimeException("the balance is not numeric format",e);
        }
        String cardNumber = "";
        if(cardBalanceRequest.getCardId().length() == 16){
            cardNumber = cardBalanceRequest.getCardId().substring(6,16);
        }else {
            throw  new CardRuntimeException("The card Id have not the length valid");
        }
        Optional<Card> cardOptional = cardRepository.findByNumber(cardNumber);
        if(cardOptional.isPresent()){
            Card card = cardOptional.get();
            Date currentDate = new Date();
            Transaction transaction = new Transaction();
            transaction.setCard(card);
            transaction.setType(TransactionTypeEnum.credit);
            transaction.setId(UUID.randomUUID());
            long millis = System.currentTimeMillis();
            transaction.setCreationDate(new Timestamp(millis));
            transaction.setValue(balance);
            if(currentDate.compareTo(card.getExpirationDate()) > 0){
                transaction.setState(TransactionStateEnum.declined);
                transactionRepository.save(transaction);
                card.setState(CardSateEnum.expired);
                cardRepository.save(card);
                throw new CardRuntimeException("The card has expired");
            }
            if(!CardSateEnum.active.equals(card.getState())){
                transaction.setState(TransactionStateEnum.declined);
                transactionRepository.save(transaction);
                throw new CardRuntimeException(String.format("The card is in state %s ",card.getState().name()));
            }
            transaction.setState(TransactionStateEnum.completed);
            transactionRepository.save(transaction);
            card.setBalance(balance);
            cardRepository.save(card);
            return String.format("The card has been charged with the balance $ %s",cardBalanceRequest.getBalance());

        }else {
            throw  new CardRuntimeException("The card Id not have been found");
        }
    }

    public String getBalance(UUID cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if(cardOptional.isPresent()){
            return String.format("\"balance\": \"$%s\"",cardOptional.get().getBalance().toString());
        }else {
            throw  new CardRuntimeException("The card Id not have been found");
        }

    }
}
