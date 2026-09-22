package com.inventory.controller;

import com.inventory.entity.Customer;
import com.inventory.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Customer> all(
            @RequestParam(required = false) String search) {

        if (search == null || search.isBlank()) {
            return repository.findAll();
        }

        return repository
                .findByFullNameContainingIgnoreCaseOrCustomerCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                        search,
                        search,
                        search,
                        search
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> one(@PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer create(@RequestBody Customer value) {
        return repository.save(value);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable Long id,
            @RequestBody Customer value) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Customer current = repository.findById(id)
                .orElseThrow();

        current.setCustomerCode(value.getCustomerCode());
        current.setFullName(value.getFullName());
        current.setLocation(value.getLocation());
        current.setPhone(value.getPhone());

        return ResponseEntity.ok(repository.save(current));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}