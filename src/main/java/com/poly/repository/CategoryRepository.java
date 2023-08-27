package com.poly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	  List<Category> findAll();
}
