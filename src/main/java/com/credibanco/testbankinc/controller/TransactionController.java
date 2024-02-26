package com.credibanco.testbankinc.controller;

import com.credibanco.testbankinc.dto.GetTransactionResponse;
import com.credibanco.testbankinc.dto.PurchaseRequest;
import com.credibanco.testbankinc.dto.PurchaseResponse;
import com.credibanco.testbankinc.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path = "/purchase")
    public ResponseEntity<PurchaseResponse> purchase(@RequestBody PurchaseRequest purchaseRequest ){
        return new ResponseEntity<>(transactionService.purchase(purchaseRequest), HttpStatus.OK);
    }

    @GetMapping(path = "/{transactionId}")
    public ResponseEntity<GetTransactionResponse> deleteCard(@PathVariable("transactionId") UUID transactionId){
        GetTransactionResponse response = transactionService.getTransaction(transactionId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }




}
