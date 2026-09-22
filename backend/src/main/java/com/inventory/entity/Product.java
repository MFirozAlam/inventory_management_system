package com.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "productcode", nullable = false, unique = true)
    private String productCode;

    @Column(name = "productname", nullable = false)
    private String productName;

    @Column(name = "category")
    private String category;

    @Column(name = "costprice", nullable = false)
    private BigDecimal costPrice;

    @Column(name = "sellprice", nullable = false)
    private BigDecimal sellPrice;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "suppliercode")
    private String supplierCode;
}
