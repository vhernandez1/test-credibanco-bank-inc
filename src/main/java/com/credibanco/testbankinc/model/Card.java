package com.credibanco.testbankinc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "card")
public class Card {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false),
    })
    private Product product;

    @Column(name = "number", nullable = false, length = 10)
    private String number;

    @Column(name = "cardholder", length = 64)
    private String cardholder;

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardSateEnum state;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
