package com.credibanco.testbankinc.repository;

import com.credibanco.testbankinc.model.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends CrudRepository<Card, UUID> {
    Optional<Card> findByNumber(String cardNumber);
}
