package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockRepository;
import com.inventory.entity.CurrentStock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductRepository repository; private final StockRepository stock;
 public ProductController(ProductRepository repository,StockRepository stock){this.repository=repository;this.stock=stock;}
 @GetMapping public List<Product> all(@RequestParam(required=false) String search,@RequestParam(required=false) String category){
   List<Product> rows;
   if(search!=null&&!search.isBlank()) rows=repository.findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCase(search,search,search,search);
   else if(category!=null&&!category.isBlank()) rows=repository.findByCategoryIgnoreCase(category);
   else rows=repository.findAll();
   if(category!=null&&!category.isBlank()&&search!=null&&!search.isBlank()) rows=rows.stream().filter(p->p.getCategory()!=null&&p.getCategory().equalsIgnoreCase(category)).toList();
   return rows;
 }
 @GetMapping("/{id}") public ResponseEntity<Product> one(@PathVariable Integer id){return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PostMapping public Product create(@RequestBody Product value){if(value.getCategory()==null||value.getCategory().isBlank())value.setCategory("General"); Product saved=repository.save(value); if(!stock.existsById(saved.getProductCode())){CurrentStock cs=new CurrentStock();cs.setProductCode(saved.getProductCode());cs.setQuantity(0);cs.setMinimumStock(5);stock.save(cs);} return saved;}
 @PutMapping("/{id}") public ResponseEntity<Product> update(@PathVariable Integer id,@RequestBody Product value){
   if(!repository.existsById(id))return ResponseEntity.notFound().build(); Product c=repository.findById(id).orElseThrow();
   c.setProductCode(value.getProductCode()); c.setProductName(value.getProductName()); c.setCategory(value.getCategory()==null||value.getCategory().isBlank()?"General":value.getCategory()); c.setCostPrice(value.getCostPrice()); c.setSellPrice(value.getSellPrice()); c.setBrand(value.getBrand()); c.setSupplierCode(value.getSupplierCode());
   return ResponseEntity.ok(repository.save(c));
 }
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Integer id){if(!repository.existsById(id))return ResponseEntity.notFound().build(); Product p=repository.findById(id).orElseThrow(); stock.deleteById(p.getProductCode()); repository.deleteById(id); return ResponseEntity.noContent().build();}
}
