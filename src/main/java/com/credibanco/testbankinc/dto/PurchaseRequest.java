package com.credibanco.testbankinc.dto;

import lombok.Data;

@Data
public class PurchaseRequest {
    private String cardId;
    private String price;
}
