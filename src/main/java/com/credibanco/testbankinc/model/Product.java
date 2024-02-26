package com.credibanco.testbankinc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    private UUID id;
    @Column(name = "number", nullable = false, length = 6)
    private String number;
    @Column(name = "name", nullable = false, length = 64)
    private String name;
}
