package com.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "productcode", nullable = false, unique = true)
    private String productCode;

    @Column(name = "productname", nullable = false)
    private String productName;

    @Column(name = "category")
    private String category = "General";

    @Column(name = "costprice", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "sellprice", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellPrice;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "suppliercode")
    private String supplierCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold = 5;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        syncFields();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        syncFields();
    }

    private void syncFields() {
        if (category == null || category.isBlank()) category = "General";
        if (status == null || status.isBlank()) status = "ACTIVE";
        if (lowStockThreshold == null || lowStockThreshold < 0) lowStockThreshold = 5;
        if (quantity == null || quantity < 0) quantity = 0;
        name = productName;
        price = sellPrice == null ? 0.0 : sellPrice.doubleValue();
    }
}
