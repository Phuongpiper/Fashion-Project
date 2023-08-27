package com.poly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Cart;
import com.poly.entity.CartProduct;
import com.poly.entity.Product;

public interface Cart_ProductRepo extends JpaRepository<CartProduct, Integer> {
	List <CartProduct> findAll();
	//CartProduct findFirstByProduct(Product product);
	 CartProduct findByCartAndProduct(Cart cart, Product product);
	void deleteById(Integer cartProductId);
	@Query("SELECT SUM(cp.product.price * cp.quantity) FROM CartProduct cp")
    Double getTotalPrice();
	
}
