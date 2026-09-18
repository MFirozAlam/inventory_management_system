package com.inventory.controller;

import com.inventory.entity.CurrentStock;
import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;
    private final StockRepository stock;

    public ProductController(ProductRepository repository, StockRepository stock) {
        this.repository = repository;
        this.stock = stock;
    }

    @GetMapping
    public List<Product> all(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String category) {
        List<Product> rows;
        if (search != null && !search.isBlank()) {
            rows = repository.findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCase(search, search, search, search);
        } else if (category != null && !category.isBlank()) {
            rows = repository.findByCategoryIgnoreCase(category);
        } else {
            rows = repository.findAll();
        }
        if (category != null && !category.isBlank() && search != null && !search.isBlank()) {
            rows = rows.stream().filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)).toList();
        }
        return rows;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> one(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public Product create(@RequestBody Product value) {
        validate(value);
        if (repository.findByProductCode(value.getProductCode()).isPresent()) {
            throw new IllegalArgumentException("Product code already exists");
        }
        value.setQuantity(0);
        value.setLowStockThreshold(value.getLowStockThreshold() == null ? 5 : value.getLowStockThreshold());
        Product saved = repository.save(value);
        CurrentStock cs = new CurrentStock(saved.getProductCode(), 0, saved.getLowStockThreshold());
        stock.save(cs);
        return saved;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product value) {
        Product current = repository.findById(id).orElse(null);
        if (current == null) return ResponseEntity.notFound().build();
        validate(value);
        if (!current.getProductCode().equals(value.getProductCode()) && repository.findByProductCode(value.getProductCode()).isPresent()) {
            throw new IllegalArgumentException("Product code already exists");
        }
        String oldCode = current.getProductCode();
        current.setProductCode(value.getProductCode());
        current.setProductName(value.getProductName());
        current.setCategory(value.getCategory() == null || value.getCategory().isBlank() ? "General" : value.getCategory());
        current.setCostPrice(value.getCostPrice());
        current.setSellPrice(value.getSellPrice());
        current.setBrand(value.getBrand());
        current.setSupplierCode(value.getSupplierCode());
        if (value.getLowStockThreshold() != null) current.setLowStockThreshold(value.getLowStockThreshold());
        Product saved = repository.save(current);
        if (!oldCode.equals(saved.getProductCode())) {
            CurrentStock oldStock = stock.findById(oldCode).orElse(null);
            CurrentStock newStock = stock.findById(saved.getProductCode()).orElseGet(() -> new CurrentStock(saved.getProductCode(), 0, saved.getLowStockThreshold()));
            if (oldStock != null) {
                newStock.setQuantity(oldStock.getQuantity());
                newStock.setMinimumStock(saved.getLowStockThreshold());
                stock.deleteById(oldCode);
            }
            stock.save(newStock);
        } else {
            CurrentStock cs = stock.findById(saved.getProductCode()).orElseGet(() -> new CurrentStock(saved.getProductCode(), saved.getQuantity(), saved.getLowStockThreshold()));
            cs.setMinimumStock(saved.getLowStockThreshold());
            saved.setQuantity(cs.getQuantity() == null ? 0 : cs.getQuantity());
            repository.save(saved);
            stock.save(cs);
        }
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Product p = repository.findById(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        stock.deleteById(p.getProductCode());
        repository.delete(p);
        return ResponseEntity.noContent().build();
    }

    private void validate(Product p) {
        if (p.getProductCode() == null || p.getProductCode().isBlank()) throw new IllegalArgumentException("Product code is required");
        if (p.getProductName() == null || p.getProductName().isBlank()) throw new IllegalArgumentException("Product name is required");
        if (p.getCostPrice() == null || p.getCostPrice().signum() < 0) throw new IllegalArgumentException("Cost price cannot be negative");
        if (p.getSellPrice() == null || p.getSellPrice().signum() < 0) throw new IllegalArgumentException("Sell price cannot be negative");
        if (p.getBrand() == null || p.getBrand().isBlank()) throw new IllegalArgumentException("Brand is required");
        if (p.getLowStockThreshold() != null && p.getLowStockThreshold() < 0) throw new IllegalArgumentException("Low-stock threshold cannot be negative");
    }
}
