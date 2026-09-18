package com.inventory.controller;

import com.inventory.entity.CurrentStock;
import com.inventory.entity.Product;
import com.inventory.entity.Purchase;
import com.inventory.entity.Sale;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.PurchaseRepository;
import com.inventory.repository.SaleRepository;
import com.inventory.repository.StockRepository;
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

    public TransactionController(PurchaseRepository purchases, SaleRepository sales, StockRepository stock, ProductRepository products) {
        this.purchases = purchases;
        this.sales = sales;
        this.stock = stock;
        this.products = products;
    }

    @GetMapping("/purchases")
    public List<Purchase> purchases() { return purchases.findAll(); }

    @GetMapping("/sales")
    public List<Sale> sales() { return sales.findAll(); }

    @PostMapping("/purchases")
    @Transactional
    public Purchase purchase(@RequestBody Purchase p) {
        validatePurchase(p);
        Product product = products.findByProductCode(p.getProductCode()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (p.getDate() == null) p.setDate(LocalDateTime.now());
        CurrentStock cs = stock.findById(p.getProductCode()).orElseGet(() -> new CurrentStock(p.getProductCode(), 0, 5));
        if (cs.getMinimumStock() == null) cs.setMinimumStock(5);
        int quantity = (cs.getQuantity() == null ? 0 : cs.getQuantity()) + p.getQuantity();
        cs.setQuantity(quantity);
        product.setQuantity(quantity);
        product.setLowStockThreshold(cs.getMinimumStock());
        stock.save(cs);
        products.save(product);
        return purchases.save(p);
    }

    @PostMapping("/sales")
    @Transactional
    public Sale sale(@RequestBody Sale s) {
        validateSale(s);
        Product product = products.findByProductCode(s.getProductCode()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        CurrentStock cs = stock.findById(s.getProductCode()).orElseThrow(() -> new IllegalArgumentException("No stock record for product"));
        int available = cs.getQuantity() == null ? 0 : cs.getQuantity();
        if (available < s.getQuantity()) throw new IllegalArgumentException("Insufficient stock. Available: " + available);
        if (s.getRevenue() == null) s.setRevenue(product.getSellPrice().multiply(BigDecimal.valueOf(s.getQuantity())));
        if (s.getDate() == null) s.setDate(LocalDateTime.now());
        if (s.getSoldBy() == null || s.getSoldBy().isBlank()) s.setSoldBy("system");
        int quantity = available - s.getQuantity();
        cs.setQuantity(quantity);
        product.setQuantity(quantity);
        stock.save(cs);
        products.save(product);
        return sales.save(s);
    }

    @DeleteMapping("/purchases/{id}")
    @Transactional
    public void deletePurchase(@PathVariable Integer id) {
        Purchase p = purchases.findById(id).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        CurrentStock cs = stock.findById(p.getProductCode()).orElseThrow(() -> new IllegalArgumentException("Stock record not found"));
        int current = cs.getQuantity() == null ? 0 : cs.getQuantity();
        int quantity = current - p.getQuantity();
        if (quantity < 0) throw new IllegalArgumentException("Cannot delete purchase because current stock is lower than the purchased quantity");
        cs.setQuantity(quantity);
        stock.save(cs);
        syncProductQuantity(p.getProductCode(), quantity);
        purchases.delete(p);
    }

    @DeleteMapping("/sales/{id}")
    @Transactional
    public void deleteSale(@PathVariable Integer id) {
        Sale s = sales.findById(id).orElseThrow(() -> new IllegalArgumentException("Sale not found"));
        CurrentStock cs = stock.findById(s.getProductCode()).orElseGet(() -> new CurrentStock(s.getProductCode(), 0, 5));
        int quantity = (cs.getQuantity() == null ? 0 : cs.getQuantity()) + s.getQuantity();
        cs.setQuantity(quantity);
        stock.save(cs);
        syncProductQuantity(s.getProductCode(), quantity);
        sales.delete(s);
    }

    private void syncProductQuantity(String code, int quantity) {
        products.findByProductCode(code).ifPresent(p -> {
            p.setQuantity(quantity);
            products.save(p);
        });
    }

    private void validatePurchase(Purchase p) {
        if (p.getProductCode() == null || p.getProductCode().isBlank()) throw new IllegalArgumentException("Product code is required");
        if (p.getSupplierCode() == null || p.getSupplierCode().isBlank()) throw new IllegalArgumentException("Supplier code is required");
        if (p.getQuantity() == null || p.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        if (p.getTotalCost() == null || p.getTotalCost().signum() < 0) throw new IllegalArgumentException("Total cost cannot be negative");
    }

    private void validateSale(Sale s) {
        if (s.getProductCode() == null || s.getProductCode().isBlank()) throw new IllegalArgumentException("Product code is required");
        if (s.getCustomerCode() == null || s.getCustomerCode().isBlank()) throw new IllegalArgumentException("Customer code is required");
        if (s.getQuantity() == null || s.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
    }
}
