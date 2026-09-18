package com.inventory.repository;
import com.inventory.entity.User; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UserRepository extends JpaRepository<User,Integer>{ Optional<User> findByUsernameAndPassword(String username,String password); Optional<User> findByUsername(String username); }