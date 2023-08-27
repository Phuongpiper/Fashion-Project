package com.poly.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.Category;
import com.poly.entity.Inventory;
import com.poly.entity.Producer;
import com.poly.entity.Product;

import com.poly.repository.InventoryRepository;
import com.poly.repository.ProductRepository;

import jakarta.validation.Valid;

@Controller
public class InventoryController {
	@Autowired
	InventoryRepository RepoInventory;
	@Autowired
	ProductRepository RepoProduct;

	@GetMapping("/inventory")
	public String inventory(Model model, @ModelAttribute("productt") Product productt,
			@ModelAttribute("inventory") Inventory inventory) {
		model.addAttribute("inventorys", RepoInventory.findAll());
		List<Product> product1 = RepoProduct.findAll();
		model.addAttribute("products", product1);
		return "/admin/inventory";
	}

	@GetMapping("/inventory/edit/")
	public String inventory_edit(Model model, @RequestParam("inventoryId") int inventoryId) {
		Inventory inventory = RepoInventory.findById(inventoryId).get();
		model.addAttribute("inventorys", RepoInventory.findAll());
		List<Product> product1 = RepoProduct.findAll();
		model.addAttribute("inventory", inventory);
		model.addAttribute("products", product1);
		return "/admin/inventory";
	}


	@PostMapping("/inventory/reset")
	public String reset(Model model) {
		Inventory inventory = new Inventory();
		model.addAttribute("inventory", inventory);
		List<Inventory> inventorys = RepoInventory.findAll();
		List<Product> products = RepoProduct.findAll();
		model.addAttribute("inventorys", inventorys);
		model.addAttribute("products", products);
		model.addAttribute("productId", null); // Đặt lại giá trị productId thành null

		return "/admin/inventory";
	}

	@PostMapping("/inventory/create")
	public String createInventory(@ModelAttribute("inventory") @Valid Inventory inventory,
			BindingResult bindingResult, @RequestParam("product") int productId, Model model) {
		
		 // Kiểm tra xem productId có được chọn hay không
	    if (productId == 0) {
	        bindingResult.rejectValue("product", "product.required", "Please select productId");
	    }
	    
		if (bindingResult.hasErrors()) {
			// Xử lý khi dữ liệu không hợp lệ, ví dụ: hiển thị thông báo lỗi và trả về trang
			// "inventory"
			model.addAttribute("error", "Invalid data");
			model.addAttribute("inventorys", RepoInventory.findAll());
			List<Product> products = RepoProduct.findAll();
			model.addAttribute("products", products);
			return "/admin/inventory";
		}

		try {
			Product product = RepoProduct.findById(productId).orElse(null);
			if (product == null) {
				model.addAttribute("error", "Product not found");
				return "/admin/inventory";
			}
			inventory.setActive(true);
			inventory.setProduct(product);
			RepoProduct.save(product);
			RepoInventory.save(inventory);

			model.addAttribute("message", "Create success");
			model.addAttribute("inventorys", RepoInventory.findAll());
			List<Product> products = RepoProduct.findAll();
			model.addAttribute("products", products);
			model.addAttribute("inventory", new Inventory());
			model.addAttribute("products", new ArrayList<Product>());

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			model.addAttribute("error", null);
			model.addAttribute("message", null);

		}

		return "/admin/inventory";
	}

	@PostMapping("/inventory/update")
	public String updateInventory(@Valid @ModelAttribute("inventory") Inventory inventory, BindingResult bindingResult,
			Model model, @RequestParam("product") int productId) {

		 // Kiểm tra xem productId có được chọn hay không
	    if (productId == 0) {
	        bindingResult.rejectValue("product", "product.required", "Please select productId");
	    }
		if (bindingResult.hasErrors()) {
			// Xử lý khi dữ liệu không hợp lệ
			List<Inventory> inventorys = RepoInventory.findAll();
			List<Product> products = RepoProduct.findAll();
			
			model.addAttribute("inventorys", inventorys);
			model.addAttribute("products", products);
			 return "/admin/inventory";
		}
		try {
			Inventory existingInventory = RepoInventory.findById(inventory.getInventoryId()).orElse(null);
			if (existingInventory != null) {
				// Cập nhật thông tin sản phẩm
				Product product = RepoProduct.findById(productId).orElse(null);
				if (product != null) {
					existingInventory.setProduct(product);
				} else {
					model.addAttribute("error", "Sản phẩm không tồn tại");

					return "/admin/inventory";
				}
				// Cập nhật các trường thông tin của kho
				existingInventory.setQuantity(inventory.getQuantity());
				existingInventory.setAddress(inventory.getAddress());
				existingInventory.setActive(true);
				RepoInventory.save(existingInventory);

				
			} else {
				model.addAttribute("error", "Kho không tồn tại");
				return "/admin/inventory";
			}
			model.addAttribute("message", "Update success ");
			// Load lại danh sách kho và các thông tin khác để hiển thị trên giao diện
			List<Inventory> inventorys = RepoInventory.findAll();
			List<Product> products = RepoProduct.findAll();
			
			model.addAttribute("inventorys", inventorys);
			model.addAttribute("products", products);
			model.addAttribute("inventory", new Inventory()); // Đặt lại đối tượng inventory rỗng
			model.addAttribute("productId", null); // Đặt lại giá trị productId thành null
		} catch (Exception e) {
			e.printStackTrace();

			model.addAttribute("error", e);
		}

		return "/admin/inventory";
	}

	
	@PostMapping("/inventory/delete")
	public String deleteInventory(@ModelAttribute("inventory") Inventory inventory,
			Model model, @RequestParam("product") int productId) {
		try {
			Inventory existingInventory = RepoInventory.findById(inventory.getInventoryId()).orElse(null);
			if (existingInventory != null) {
				// Cập nhật thông tin sản phẩm
				Product product = RepoProduct.findById(productId).orElse(null);
				if (product != null) {
					existingInventory.setProduct(product);
				} else {
					model.addAttribute("error", "Sản phẩm không tồn tại");

					return "/admin/inventory";
				}
				// Cập nhật các trường thông tin của kho
				existingInventory.setQuantity(inventory.getQuantity());
				existingInventory.setAddress(inventory.getAddress());
				existingInventory.setActive(false);
				RepoInventory.save(existingInventory);

				model.addAttribute("message", "Delete success ");
			} else {
				model.addAttribute("error", "Kho không tồn tại");
				return "/admin/inventory";
			}

			// Load lại danh sách kho và các thông tin khác để hiển thị trên giao diện
			List<Inventory> inventorys = RepoInventory.findAll();
			List<Product> products = RepoProduct.findAll();
			model.addAttribute("inventorys", inventorys);
			model.addAttribute("products", products);
			model.addAttribute("inventory", new Inventory()); // Đặt lại đối tượng inventory rỗng
			model.addAttribute("productId", null); // Đặt lại giá trị productId thành null
		} catch (Exception e) {
			e.printStackTrace();

			model.addAttribute("error", e);
		}

		return "/admin/inventory";
	}
}
