package com.credibanco.testbankinc.dto;

import com.credibanco.testbankinc.model.TransactionStateEnum;
import com.credibanco.testbankinc.model.TransactionTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionResponse {

    private String state;

    private Date creationDate;

    private String type;

    private BigDecimal value;
}
