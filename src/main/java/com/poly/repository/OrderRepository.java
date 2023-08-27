package com.poly.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Order;
import com.poly.entity.User;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	@Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi")
	BigDecimal getTotalRevenue();

	@Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE YEAR(oi.order.orderDate) = :year AND MONTH(oi.order.orderDate) = :month")
	BigDecimal getRevenueByMonth(int year, int month);

	@Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE YEAR(oi.order.orderDate) = :year")
	BigDecimal getRevenueByYear(int year);

	@Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.orderDate BETWEEN :startDate AND :endDate")
	BigDecimal getRevenueByDateRange(Date startDate, Date endDate);

	List<Order> findByUser(User user);
	Page<Order> findByUser(User user, Pageable pageable);
	Page<Order> findByUserUsernameContaining(String username, Pageable pageable);
	
	
	
	

	
}
