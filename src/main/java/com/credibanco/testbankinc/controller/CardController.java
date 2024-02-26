package com.credibanco.testbankinc.controller;

import com.credibanco.testbankinc.dto.CardBalanceRequest;
import com.credibanco.testbankinc.dto.CardEnrollRequest;
import com.credibanco.testbankinc.dto.CardResponse;
import com.credibanco.testbankinc.exception.CardRuntimeException;
import com.credibanco.testbankinc.model.Card;
import com.credibanco.testbankinc.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/card")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(path = "/{productid}/number")
    public ResponseEntity<CardResponse> getCardNumber(@PathVariable("productid") UUID productId){
        Card card =cardService.createCard(productId);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setCardNumber(card.getProduct().getNumber()+card.getNumber());
        cardResponse.setId(card.getId());
        return new ResponseEntity<>(cardResponse,HttpStatus.OK);


    }

    @PostMapping(path = "/enroll")
    public ResponseEntity<String> cardEnroll(@RequestBody CardEnrollRequest cardEnrollRequest ){
        String cardEnrolledMessage = cardService.cardEnroll(cardEnrollRequest.getCardId());
        return new ResponseEntity<>(cardEnrolledMessage,HttpStatus.OK);
     }

    @DeleteMapping(path = "/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable("cardId") UUID cardId){
        String response = cardService.deleteCard(cardId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping(path = "/balance")
    public ResponseEntity<String> balance(@RequestBody CardBalanceRequest cardBalanceRequest ){
        String messageSuccessful = cardService.balance(cardBalanceRequest);
        return new ResponseEntity<>(messageSuccessful,HttpStatus.OK);
    }

    @GetMapping(path = "/balance/{cardId}")
    public ResponseEntity<String> balance(@PathVariable("cardId") UUID cardId){
        String balance = cardService.getBalance(cardId);
        return new ResponseEntity<>(balance,HttpStatus.OK);
    }


}
