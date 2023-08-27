package com.poly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Producer;

public interface ProducerRepo extends JpaRepository<Producer, Integer> {
	List<Producer> findAll();
}
