package com.inventory.controller;
import com.inventory.repository.*; import com.inventory.entity.*;
import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
 private final ProductRepository products; private final CustomerRepository customers; private final SupplierRepository suppliers; private final StockRepository stock; private final SaleRepository sales; private final PurchaseRepository purchases;
 public DashboardController(ProductRepository p,CustomerRepository c,SupplierRepository s,StockRepository st,SaleRepository sa,PurchaseRepository pu){products=p;customers=c;suppliers=s;stock=st;sales=sa;purchases=pu;}
 @GetMapping public Map<String,Object> get(){
  BigDecimal revenue=sales.findAll().stream().map(Sale::getRevenue).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
  BigDecimal cost=purchases.findAll().stream().map(Purchase::getTotalCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
  int units=stock.findAll().stream().map(CurrentStock::getQuantity).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
  return Map.of("products",products.count(),"customers",customers.count(),"suppliers",suppliers.count(),"stockUnits",units,"revenue",revenue,"purchaseCost",cost,"sales",sales.count(),"purchases",purchases.count());
 }
}
