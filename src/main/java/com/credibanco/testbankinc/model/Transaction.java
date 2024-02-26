package com.credibanco.testbankinc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false),
    })
    private Card card;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStateEnum state;

    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    @Column(name = "value", nullable = false)
    private BigDecimal value;




}
