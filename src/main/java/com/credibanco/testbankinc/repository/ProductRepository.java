package com.credibanco.testbankinc.repository;

import com.credibanco.testbankinc.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
}
