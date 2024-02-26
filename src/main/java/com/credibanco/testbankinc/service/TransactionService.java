package com.credibanco.testbankinc.service;

import com.credibanco.testbankinc.dto.GetTransactionResponse;
import com.credibanco.testbankinc.dto.PurchaseRequest;
import com.credibanco.testbankinc.dto.TransactionResponse;
import com.credibanco.testbankinc.dto.ReverseTransactionRequest;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.*;
import com.credibanco.testbankinc.repository.CardRepository;
import com.credibanco.testbankinc.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse purchase(PurchaseRequest purchaseRequest) {
        BigDecimal price;
        try {
            price = new BigDecimal(purchaseRequest.getPrice());
            if(price.compareTo(BigDecimal.ZERO) < 0){
                throw new CardRuntimeException("the price should be positive");
            }
        }catch (NumberFormatException e){
            throw new CardRuntimeException("the price is not numeric format",e);
        }
        String cardNumber = "";
        if(purchaseRequest.getCardId().length() <= 16){
            cardNumber = purchaseRequest.getCardId().substring(6,16);
        }else {
            throw  new CardRuntimeException("The card Id have not the length valid");
        }
        Optional<Card> cardOptional = cardRepository.findByNumber(cardNumber);
        if(cardOptional.isPresent()){
            Card card = cardOptional.get();
            Date currentDate = new Date();
            Transaction transaction = new Transaction();
            transaction.setCard(card);
            transaction.setType(TransactionTypeEnum.debit);
            transaction.setId(UUID.randomUUID());
            long millis = System.currentTimeMillis();
            transaction.setCreationDate(new Timestamp(millis));
            transaction.setValue(price);
            if(card.getBalance().compareTo(price) < 0){
                transaction.setState(TransactionStateEnum.declined);
                transactionRepository.save(transaction);
                throw  new CardRuntimeException("The transaction could not be completed due to insufficient funds on the card.");
            }
            if(currentDate.after(card.getExpirationDate())){
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

            card.setBalance(card.getBalance().subtract(price));
            cardRepository.save(card);
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setTransactionId(transaction.getId());
            transactionResponse.setMessage(String.format("The purchase is completed successful for $ %s",price));
            return transactionResponse;

        }else {
            throw  new CardRuntimeException("The card Id not have been found");
        }
    }

    public GetTransactionResponse getTransaction(UUID transactionId) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
        if(transactionOptional.isPresent()){
            Optional<GetTransactionResponse> getTransactionResponseOptional = transactionOptional
                    .map(x->new GetTransactionResponse(x.getState().name(),x.getCreationDate(),x.getType().name(),x.getValue()));
            return getTransactionResponseOptional.get();
        }else {
            throw  new CardRuntimeException("The transaction Id not have been found");
        }
    }

    public TransactionResponse reverse(ReverseTransactionRequest reverseTransactionRequest) {
        String cardNumber = "";
        if(reverseTransactionRequest.getCardId().length() == 16){
            cardNumber = reverseTransactionRequest.getCardId().substring(6,16);
        }else {
            throw  new CardRuntimeException("The card Id have not the length valid");
        }
        Optional<Transaction> transactionOptional = transactionRepository
                .findByIdAndCardNumber(reverseTransactionRequest.getTransactionId(),cardNumber);
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            long millisCurrentTime = System.currentTimeMillis();
            long millisIn24Hours = millisCurrentTime + (24 * 60 * 60 * 1000);
            Timestamp timestampIn24Hours = new Timestamp(millisIn24Hours);
            if(!TransactionStateEnum.completed.equals(transaction.getState())){
                throw new CardRuntimeException(String.format("The transaction is in state %s",transaction.getState()));
            }
            if(timestampIn24Hours.before(transaction.getCreationDate())){
                throw  new CardRuntimeException("The transaction time is greater than 24 hours from the current time.");
            }
            transaction.setUpdateDate(new Timestamp(millisCurrentTime));
            transaction.setState(TransactionStateEnum.reversed);
            transaction.setType(TransactionTypeEnum.credit);
            transactionRepository.save(transaction);
            Card card = transaction.getCard();
            card.setBalance(card.getBalance().add(transaction.getValue()));
            cardRepository.save(card);
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setTransactionId(transaction.getId());
            transactionResponse.setMessage(String.format("The reverse is completed successful for $ %s",transaction.getValue()));
            return transactionResponse;

        }else{
            throw  new CardRuntimeException("The transaction Id not have been found");
        }
    }
}
