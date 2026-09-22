package com.inventory.controller;

import com.inventory.entity.*;
import com.inventory.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final PurchaseRepository purchases;
    private final SaleRepository sales;
    private final StockRepository stock;
    private final ProductRepository products;

    public TransactionController(
            PurchaseRepository p,
            SaleRepository s,
            StockRepository st,
            ProductRepository pr) {
        purchases = p;
        sales = s;
        stock = st;
        products = pr;
    }

    @GetMapping("/purchases")
    public List<Purchase> purchases() {
        return purchases.findAll();
    }

    @GetMapping("/sales")
    public List<Sale> sales() {
        return sales.findAll();
    }

    @PostMapping("/purchases")
    @Transactional
    public Purchase purchase(@RequestBody Purchase p) {

        if (p.getQuantity() == null || p.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        products.findByProductCode(p.getProductCode())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (p.getDate() == null) {
            p.setDate(LocalDateTime.now());
        }

        if (p.getTotalCost() == null) {
            throw new IllegalArgumentException("Total cost is required");
        }

        CurrentStock cs = stock.findById(p.getProductCode())
                .orElseGet(() -> {
                    CurrentStock n = new CurrentStock();
                    n.setProductCode(p.getProductCode());
                    n.setQuantity(0);
                    n.setMinimumStock(5);
                    return n;
                });

        if (cs.getMinimumStock() == null) {
            cs.setMinimumStock(5);
        }

        cs.setQuantity(
                (cs.getQuantity() == null ? 0 : cs.getQuantity())
                        + p.getQuantity()
        );

        stock.save(cs);

        return purchases.save(p);
    }

    @PostMapping("/sales")
    @Transactional
    public Sale sale(@RequestBody Sale s) {

        if (s.getQuantity() == null || s.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CurrentStock cs = stock.findById(s.getProductCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("No stock record for product"));

        if (cs.getQuantity() < s.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + cs.getQuantity()
            );
        }

        Product p = products.findByProductCode(s.getProductCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        if (s.getRevenue() == null) {
            s.setRevenue(
                    p.getSellPrice()
                            .multiply(BigDecimal.valueOf(s.getQuantity()))
            );
        }

        if (s.getDate() == null) {
            s.setDate(LocalDateTime.now());
        }

        if (s.getSoldBy() == null || s.getSoldBy().isBlank()) {
            s.setSoldBy("system");
        }

        cs.setQuantity(cs.getQuantity() - s.getQuantity());

        stock.save(cs);

        return sales.save(s);
    }

    @DeleteMapping("/purchases/{id}")
    @Transactional
    public void deletePurchase(@PathVariable Integer id) {

        Purchase p = purchases.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Purchase not found"));

        CurrentStock cs = stock.findById(p.getProductCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("Stock record not found"));

        int currentQuantity = cs.getQuantity() == null
                ? 0
                : cs.getQuantity();

        int newQuantity = currentQuantity - p.getQuantity();

        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Cannot delete purchase because current stock is lower than the purchased quantity"
            );
        }

        cs.setQuantity(newQuantity);

        stock.save(cs);

        purchases.delete(p);
    }

    @DeleteMapping("/sales/{id}")
    @Transactional
    public void deleteSale(@PathVariable Integer id) {

        Sale s = sales.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sale not found"));

        CurrentStock cs = stock.findById(s.getProductCode())
                .orElseGet(() -> {
                    CurrentStock n = new CurrentStock();
                    n.setProductCode(s.getProductCode());
                    n.setQuantity(0);
                    n.setMinimumStock(5);
                    return n;
                });

        int currentQuantity = cs.getQuantity() == null
                ? 0
                : cs.getQuantity();

        cs.setQuantity(currentQuantity + s.getQuantity());

        stock.save(cs);

        sales.delete(s);
    }
}