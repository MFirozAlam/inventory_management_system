package com.inventory.repository;
import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProductRepository extends JpaRepository<Product,Integer>{
 Optional<Product> findByProductCode(String code);
 List<Product> findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCase(String n,String c,String b,String category);
 List<Product> findByCategoryIgnoreCase(String category);
}
