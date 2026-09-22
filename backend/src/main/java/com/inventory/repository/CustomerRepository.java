package com.inventory.repository;
import com.inventory.entity.Customer; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CustomerRepository extends JpaRepository<Customer,Long>{
    Optional<Customer> findByCustomerCode(String code);
    List<Customer> findByFullNameContainingIgnoreCaseOrCustomerCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrPhoneContainingIgnoreCase(String a,String b,String c,String d);
}