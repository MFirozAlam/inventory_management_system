package com.inventory.controller;

import com.inventory.entity.CurrentStock;
import com.inventory.repository.StockRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/stock")
public class StockController {
 private final StockRepository repo; public StockController(StockRepository repo){this.repo=repo;}
 @GetMapping public List<CurrentStock> all(){return repo.findAll();}
 @GetMapping("/low") public List<CurrentStock> low(){return repo.findAll().stream().filter(s->s.getMinimumStock()!=null&&s.getQuantity()!=null&&s.getQuantity()<=s.getMinimumStock()).toList();}
 @GetMapping("/{code}") public CurrentStock one(@PathVariable String code){return repo.findById(code).orElseThrow();}
 @PutMapping("/{code}/minimum") public CurrentStock minimum(@PathVariable String code,@RequestParam Integer value){if(value<0)throw new IllegalArgumentException("Minimum stock cannot be negative"); CurrentStock s=repo.findById(code).orElseThrow(); s.setMinimumStock(value); return repo.save(s);}
}
