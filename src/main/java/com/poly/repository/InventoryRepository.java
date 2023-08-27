package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
	@Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
	Inventory findByProductId(@Param("productId") int productId);

}
