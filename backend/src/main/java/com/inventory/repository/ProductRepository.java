package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String code);
    List<Product> findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String code, String brand, String category);
    List<Product> findByCategoryIgnoreCase(String category);
}
