package com.inventory.repository;

import com.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findBySupplierCode(String code);
    List<Supplier> findByFullNameContainingIgnoreCaseOrSupplierCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String code, String location, String phone);
}
