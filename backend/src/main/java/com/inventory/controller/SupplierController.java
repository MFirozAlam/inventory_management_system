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
    public SupplierController(SupplierRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Supplier> all(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) return repository.findAll();
        return repository.findByFullNameContainingIgnoreCaseOrSupplierCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrPhoneContainingIgnoreCase(search, search, search, search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> one(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier create(@RequestBody Supplier value) {
        validate(value);
        if (repository.findBySupplierCode(value.getSupplierCode()).isPresent()) throw new IllegalArgumentException("Supplier code already exists");
        return repository.save(value);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier value) {
        Supplier current = repository.findById(id).orElse(null);
        if (current == null) return ResponseEntity.notFound().build();
        validate(value);
        repository.findBySupplierCode(value.getSupplierCode()).ifPresent(existing -> { if (!existing.getId().equals(id)) throw new IllegalArgumentException("Supplier code already exists"); });
        current.setSupplierCode(value.getSupplierCode());
        current.setFullName(value.getFullName());
        current.setLocation(value.getLocation());
        current.setPhone(value.getPhone());
        return ResponseEntity.ok(repository.save(current));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void validate(Supplier s) {
        if (s.getSupplierCode() == null || s.getSupplierCode().isBlank()) throw new IllegalArgumentException("Supplier code is required");
        if (s.getFullName() == null || s.getFullName().isBlank()) throw new IllegalArgumentException("Supplier name is required");
        if (s.getLocation() == null || s.getLocation().isBlank()) throw new IllegalArgumentException("Location is required");
        if (s.getPhone() == null || s.getPhone().isBlank()) throw new IllegalArgumentException("Phone is required");
    }
}
