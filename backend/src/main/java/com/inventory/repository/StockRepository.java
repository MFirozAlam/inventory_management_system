package com.inventory.repository;
import com.inventory.entity.CurrentStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface StockRepository extends JpaRepository<CurrentStock,String>{
 List<CurrentStock> findByQuantityLessThanEqual(Integer quantity);
}
