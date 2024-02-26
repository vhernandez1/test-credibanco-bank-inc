package com.credibanco.testbankinc.repository;

import com.credibanco.testbankinc.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndCardNumber(UUID transactionId, String cardNumber);
}
