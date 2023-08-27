package com.poly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Category;
import com.poly.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findAll();

	void deleteById(int id);

	List<Product> findFirst10ByOrderByDiscountDesc();

	/* Tìm theo key */
	@Query("SELECT o FROM Product o WHERE o.productName LIKE ?1")
	Page<Product> findAllByNameLike(String keywords, Pageable pageable);

	/* Tìm theo tên và loại */
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.productName LIKE ?2")
	Page<Product> findAllByCategoryAndNameLike(int categoryId, String keywords, Pageable pageable);

	/* Phân loại */
	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
	Page<Product> findByCategory(@Param("categoryId") int categoryId, Pageable pageable);

	List<Product> findByCategory(int categoryId);

	@Query("SELECT p.category.categoryName, COUNT(p) FROM Product p GROUP BY p.category.categoryName")
	List<Object[]> countProductsByCategory();

	@Query("SELECT p.producer.producerName, COUNT(p) FROM Product p GROUP BY p.producer.producerName")
	List<Object[]> countProductsByProducer();

	@Query("SELECT o FROM Product o WHERE o.productId LIKE ?1")
	Page<Product> FindByIdProduct(int productId, Pageable pageable);
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.productId = :productId"
	 * ) void decreaseProductQuantity(@Param("productId") int
	 * productId, @Param("quantity") int quantity);
	 */
	
	@Query("SELECT p.quantity FROM Product p WHERE p.productId = :productId")
    int getProductQuantity(@Param("productId") int productId);

}
