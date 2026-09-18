package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currentstock")
public class CurrentStock {

    @Id
    @Column(name = "productcode")
    private String productCode;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "minimumstock", nullable = false)
    private Integer minimumStock = 5;
}