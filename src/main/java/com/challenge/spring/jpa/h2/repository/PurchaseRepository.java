package com.challenge.spring.jpa.h2.repository;

import com.challenge.spring.jpa.h2.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
  ResponseEntity<Purchase> getPurchaseById(final long id);
}
