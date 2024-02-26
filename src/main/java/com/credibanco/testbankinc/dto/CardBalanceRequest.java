package com.credibanco.testbankinc.dto;

import lombok.Data;

@Data
public class CardBalanceRequest {
    private String cardId;
    private String balance;
}
