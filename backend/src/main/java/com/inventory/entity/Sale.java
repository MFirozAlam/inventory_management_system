package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "salesinfo")
public class Sale {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "salesid")
 private Integer id;

 @Column(name = "date", nullable = false)
 private LocalDateTime date;

 @Column(name = "productcode", nullable = false)
 private String productCode;

 @Column(name = "customercode", nullable = false)
 private String customerCode;

 @Column(nullable = false)
 private Integer quantity;

 @Column(nullable = false, precision = 12, scale = 2)
 private BigDecimal revenue;

 @Column(name = "soldby", nullable = false)
 private String soldBy;
}