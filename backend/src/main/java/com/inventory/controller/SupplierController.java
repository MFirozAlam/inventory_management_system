package com.inventory.controller;
import com.inventory.entity.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
 private final SupplierRepository repository;
 public SupplierController(SupplierRepository repository) { this.repository=repository; }
 @GetMapping public List<Supplier> all(@RequestParam(required=false) String search) {
   if(search==null || search.isBlank()) return repository.findAll();
   return repository.findByFullNameContainingIgnoreCaseOrSupplierCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrPhoneContainingIgnoreCase(search,search,search,search);
 }
 @GetMapping("/{id}") public ResponseEntity<Supplier> one(@PathVariable Integer id) { return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
 @PostMapping public Supplier create(@RequestBody Supplier value) { return repository.save(value); }
 @PutMapping("/{id}") public ResponseEntity<Supplier> update(@PathVariable Integer id,@RequestBody Supplier value) {
   if(!repository.existsById(id)) return ResponseEntity.notFound().build(); 
   Supplier current=repository.findById(id).orElseThrow(); 
   current.setSupplierCode(value.getSupplierCode()); current.setFullName(value.getFullName()); current.setLocation(value.getLocation()); current.setPhone(value.getPhone());
   return ResponseEntity.ok(repository.save(current));
 }
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Integer id) { if(!repository.existsById(id)) return ResponseEntity.notFound().build(); repository.deleteById(id); return ResponseEntity.noContent().build(); }
}
