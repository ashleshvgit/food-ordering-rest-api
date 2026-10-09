package com.ashlesh.mycoolproject.repository;

import com.ashlesh.mycoolproject.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}