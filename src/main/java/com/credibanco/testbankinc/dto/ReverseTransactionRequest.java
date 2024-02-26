package com.credibanco.testbankinc.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReverseTransactionRequest {
    private String cardId;
    private UUID transactionId;
}
