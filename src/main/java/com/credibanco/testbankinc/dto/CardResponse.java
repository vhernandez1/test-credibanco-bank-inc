package com.credibanco.testbankinc.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class CardResponse {
    private UUID id;
    private String cardNumber;
}
