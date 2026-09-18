package com.inventory.repository;
import com.inventory.entity.Purchase; import org.springframework.data.jpa.repository.JpaRepository;
public interface PurchaseRepository extends JpaRepository<Purchase,Integer>{}