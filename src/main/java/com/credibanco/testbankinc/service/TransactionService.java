package com.credibanco.testbankinc.service;

import com.credibanco.testbankinc.dto.GetTransactionResponse;
import com.credibanco.testbankinc.dto.PurchaseRequest;
import com.credibanco.testbankinc.dto.PurchaseResponse;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.*;
import com.credibanco.testbankinc.repository.CardRepository;
import com.credibanco.testbankinc.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public PurchaseResponse purchase(PurchaseRequest purchaseRequest) {
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
            transaction.setCreationDate(new Date());//TODO Corregir esto ha fecha y hora
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
            PurchaseResponse purchaseResponse = new PurchaseResponse();
            purchaseResponse.setTransactionId(transaction.getId());
            purchaseResponse.setMessage(String.format("The purchase is completed successful for $ %s",price));
            return purchaseResponse;

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
}
