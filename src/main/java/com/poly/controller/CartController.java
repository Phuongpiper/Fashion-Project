package com.poly.controller;

import com.poly.entity.Cart;
import com.poly.entity.CartProduct;
import com.poly.entity.Product;
import com.poly.repository.CartRepository;
import com.poly.repository.Cart_ProductRepo;
import com.poly.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {
	@Autowired
	private Cart_ProductRepo cartProductRepo;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	HttpServletRequest request;

	@GetMapping("/cart")
	public String cart(Model model) {
		Integer cartId = (Integer) request.getSession().getAttribute("cartId");

		if (cartId != null) {
			Cart cart = cartRepository.findById(cartId).orElse(null);

			if (cart != null) {
				List<CartProduct> cartProducts = cart.getCartProducts();
				model.addAttribute("cartProducts", cartProducts);

				Double totalPrice = calculateTotalPrice(cartProducts);
				model.addAttribute("totalPrice", totalPrice);
			}
		}

		return "cart";
	}

	private Double calculateTotalPrice(List<CartProduct> cartProducts) {
	    Double totalPrice = 0.0;

	    for (CartProduct cartProduct : cartProducts) {
	        Double productPrice = cartProduct.getProduct().getPrice().doubleValue();
	        Double discount = cartProduct.getProduct().getDiscount().doubleValue();
	        int quantity = cartProduct.getQuantity();
	        Double discountedPrice = productPrice - (productPrice * discount);
	        totalPrice += discountedPrice * quantity;
	    }

	    return totalPrice;
	}


	@GetMapping("/cart/{productId}")
	public String addToCart(Model model, @PathVariable("productId") Integer productId) {
		Product product = productRepo.findById(productId).orElse(null);

		if (product != null) {
			Integer cartId = (Integer) request.getSession().getAttribute("cartId");

			if (cartId != null) {
				// Lấy đối tượng Cart từ cơ sở dữ liệu
				Cart cart = cartRepository.findById(cartId).orElse(null);

				if (cart != null) {
					// Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
					CartProduct existingCartProduct = cartProductRepo.findByCartAndProduct(cart, product);

					if (existingCartProduct != null) {
						// Nếu sản phẩm đã tồn tại trong giỏ hàng, tăng số lượng lên 1
						existingCartProduct.setQuantity(existingCartProduct.getQuantity() + 1);
						cartProductRepo.save(existingCartProduct);
					} else {
						// Nếu sản phẩm chưa tồn tại trong giỏ hàng, thêm sản phẩm mới
						CartProduct cartProduct = new CartProduct();
						cartProduct.setProduct(product);
						cartProduct.setQuantity(1); // Số lượng mặc định là 1
						cartProduct.setCart(cart);
						cartProduct.setStatus(false);
						cartProductRepo.save(cartProduct);
					}
				}
			}
		}

		Integer cartId = (Integer) request.getSession().getAttribute("cartId");

		if (cartId != null) {
			Cart cart = cartRepository.findById(cartId).orElse(null);

			if (cart != null) {
				List<CartProduct> cartProducts = cart.getCartProducts();
				model.addAttribute("cartProducts", cartProducts);

				Double totalPrice = calculateTotalPrice(cartProducts);
				model.addAttribute("totalPrice", totalPrice);
			}
		}

		return "cart";
	}

	@GetMapping("/cart/delete/{cartProductId}")
	public String removeFromCart(Model model, @PathVariable("cartProductId") int cartProductId) {
		// Xóa sản phẩm khỏi giỏ hàng dựa vào cartProductId
		cartProductRepo.deleteById(cartProductId);

		Integer cartId = (Integer) request.getSession().getAttribute("cartId");

		if (cartId != null) {
			Cart cart = cartRepository.findById(cartId).orElse(null);

			if (cart != null) {
				List<CartProduct> cartProducts = cart.getCartProducts();
				model.addAttribute("cartProducts", cartProducts);

				Double totalPrice = calculateTotalPrice(cartProducts);
				model.addAttribute("totalPrice", totalPrice);
			}
		}
		return "redirect:/cart";
	}

	@GetMapping("/cart/update/{cartProductId}/{action}")
	public String updateQuantity(@PathVariable("cartProductId") int cartProductId,
			@PathVariable("action") String action, Model model) {
		CartProduct cartProduct = cartProductRepo.findById(cartProductId).orElse(null);

		if (cartProduct != null) {
			if (action.equals("increase")) {
				// Tăng số lượng lên 1
				cartProduct.setQuantity(cartProduct.getQuantity() + 1);
			} else if (action.equals("decrease")) {
				// Giảm số lượng đi 1
				if (cartProduct.getQuantity() > 1) {
					cartProduct.setQuantity(cartProduct.getQuantity() - 1);
				}
			}

			cartProductRepo.save(cartProduct);
		}
		
		Integer cartId = (Integer) request.getSession().getAttribute("cartId");

		if (cartId != null) {
			Cart cart = cartRepository.findById(cartId).orElse(null);

			if (cart != null) {
				List<CartProduct> cartProducts = cart.getCartProducts();
				Double totalPrice = calculateTotalPrice(cartProducts);
				model.addAttribute("totalPrice", totalPrice);
			}
		}
		return "redirect:/cart";
	}

}
