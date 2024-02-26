package com.credibanco.testbankinc.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PurchaseResponse {
    private UUID transactionId;
    private String message;
}
