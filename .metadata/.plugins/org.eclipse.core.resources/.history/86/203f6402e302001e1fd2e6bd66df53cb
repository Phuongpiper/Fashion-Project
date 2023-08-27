package com.example.demo.Dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.Model.Product;
import com.example.demo.Model.Report;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {
	List<Product> findByPriceBetween(double minPrice, double maxPrice);


	Page<Product> findAllByNameLike(String keywords, Pageable pageable);


	@Query("SELECT new Report(o.category, sum(o.price), count(o)) " + "FROM Product o " + "GROUP BY o.category "
			+ "ORDER BY sum(o.price) DESC")
	List<Report> getInventoryByCategory();

}
