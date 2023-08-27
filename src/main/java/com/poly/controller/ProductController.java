package com.poly.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.Category;
import com.poly.entity.Producer;
import com.poly.entity.Product;
import com.poly.entity.User;
import com.poly.repository.CategoryRepository;
import com.poly.repository.ProducerRepo;
import com.poly.repository.ProductRepository;
import com.poly.service.SessionService;

import jakarta.validation.Valid;

@Controller
public class ProductController {
	@Autowired
	CategoryRepository dao1;
	@Autowired
	ProductRepository dao;
	@Autowired
	ProducerRepo producerRepo;
	@Autowired
	SessionService session;

	@GetMapping("/shop")
	public String paginateAndSearch(Model model, 
	        @RequestParam("p") Optional<Integer> p, 
	        @RequestParam("keywords") Optional<String> keywords,
	        @RequestParam("value") Optional<Integer> value) {
	    // Phân trang
	    Pageable pageable = PageRequest.of(p.orElse(0), 6);
	    Page<Product> page;
	    
	    // Tìm kiếm
	    if (keywords.isPresent() && !keywords.get().isEmpty()) {
	        String kw = "%" + keywords.get() + "%";
	        if (value.isPresent()) {
	            page = dao.findAllByCategoryAndNameLike(value.get(), kw, pageable);
	        } else {
	            page = dao.findAllByNameLike(kw, pageable);
	        }
	    } else {
	        if (value.isPresent()) {
	            page = dao.findByCategory(value.get(), pageable);
	        } else {
	            page = dao.findAll(pageable);
	        }
	    }
	    
	    // Kiểm tra kết quả tìm kiếm
	    if (page.isEmpty()) {
	        List<Product> allProducts;
	        if (value.isPresent()) {
	            allProducts = dao.findByCategory(value.get());
	        } else {
	            allProducts = dao.findAll();
	        }
	        model.addAttribute("message", "Không có sản phẩm phù hợp.");
	    } else {
	        model.addAttribute("products", page);
	    }
	    
	    return "shop";
	}

	@GetMapping("/shop/{productId}")
	public String detal(Model model, @PathVariable int productId) {
		Product item = dao.findById(productId).orElse(null);
		model.addAttribute("item", item);
		List<Product> products = dao.findAll();
		model.addAttribute("products", products);
		return "detail";
	}

	@GetMapping("/admin")
	public String admin(Model model, @ModelAttribute("product") Product product,
	        @RequestParam(value = "keywords", required = false) String keywords,
	        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
	        @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

	    // Sử dụng phân trang
	    Pageable pageable = PageRequest.of(page, size);

	    // Tìm kiếm sản phẩm với từ khóa (nếu có)
	    Page<Product> productPage;
	    if (keywords != null && !keywords.isEmpty()) {
	        productPage = dao.findAllByNameLike("%" + keywords + "%", pageable);
	    } else {
	        productPage = dao.findAll(pageable);
	    }
	    List<Product> product1 = productPage.getContent();

	    // Thêm danh sách sản phẩm và các thông tin khác vào model
	    model.addAttribute("products", product1);
	    model.addAttribute("categorys", dao1.findAll());
	    model.addAttribute("producers", producerRepo.findAll());

	    // Thêm các thông tin phân trang vào model
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", productPage.getTotalPages());
	    model.addAttribute("size", size);
	    model.addAttribute("keywords", keywords);

	    return "/admin/admin";
	}

	@GetMapping("/admin/edit/")
	public String edit(Model model, @RequestParam("productId") int productId, Product product,
	                   @RequestParam(value = "page", defaultValue = "0") int page,
	                   @RequestParam(value = "size", defaultValue = "5") int size) {
	    product = dao.findById(productId).get();
	    model.addAttribute("product", product);
	    List<Category> category = dao1.findAll();
	    model.addAttribute("categorys", category);
	    List<Producer> producer = producerRepo.findAll();
	    model.addAttribute("producers", producer);

	    // Thêm phân trang vào model
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Product> productPage = dao.findAll(pageable);
	    model.addAttribute("products", productPage.getContent());
	    model.addAttribute("totalPages", productPage.getTotalPages());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("size", size);

	    return "/admin/admin";
	}


	@PostMapping("/admin/create")
	public String createProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
            Model model, @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category_id") int categoryId,
            @RequestParam("producer_id") int producerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
		
		// Kiểm tra xem trường category và producer có được chọn hay không
	    if (categoryId == 0) {
	        bindingResult.rejectValue("category", "category.required", "Please select item type");
	    }
	    if (producerId == 0) {
	        bindingResult.rejectValue("producer", "producer.required", "Please select producer");
	    }

		try {

			if (!imageFile.isEmpty()) {
				// Lưu file hình ảnh vào thư mục lưu trữ
				String fileName = imageFile.getOriginalFilename();
				String uploadDir = "C:/uploads";
				File directory = new File(uploadDir);
				if (!directory.exists()) {
					directory.mkdirs();
				} // Đường dẫn thư mục lưu trữ hình ảnh (tương đối)

				String filePath = uploadDir + "/" + fileName;
				File dest = new File(filePath);
				imageFile.transferTo(dest);

				// Gán đường dẫn hoặc chuỗi đại diện cho trường 'image' trong đối tượng
				// 'Product'
				product.setImage(filePath);
				model.addAttribute("hasMessage", false);
			} else {
				model.addAttribute("hasMessage", true);
				
				// Cập nhật danh sách sản phẩm theo trang hiện tại
				Pageable pageable = PageRequest.of(page, size);
				Page<Product> productPage = dao.findAll(pageable);
				
				model.addAttribute("totalPages", productPage.getTotalPages());
				model.addAttribute("currentPage", page);
				model.addAttribute("size", size);
				
				model.addAttribute("products", productPage.getContent());
				model.addAttribute("categorys", dao1.findAll());
				model.addAttribute("producers", producerRepo.findAll());
				
				return "/admin/admin";
			}
			// if (product.getCategory()==null) {
			// bindingResult.rejectValue("category.categoryId", "NotNull.product.category");
			// }

			if (bindingResult.getErrorCount() > 1) {
				
				// Cập nhật danh sách sản phẩm theo trang hiện tại
				Pageable pageable = PageRequest.of(page, size);
				Page<Product> productPage = dao.findAll(pageable);
				
				model.addAttribute("totalPages", productPage.getTotalPages());
				model.addAttribute("currentPage", page);
				model.addAttribute("size", size);
				
				model.addAttribute("products", productPage.getContent());
				model.addAttribute("categorys", dao1.findAll());
				model.addAttribute("producers", producerRepo.findAll());
				
				return "/admin/admin";
			}
			Category category1 = dao1.findById(categoryId).get();
			Producer producer1 = producerRepo.findById(producerId).get();

			product.setCategory(category1);
			product.setProducer(producer1);
			product.setActive(true);
			dao.save(product);

			// Cập nhật danh sách sản phẩm theo trang hiện tại
			Pageable pageable = PageRequest.of(page, size);
			Page<Product> productPage = dao.findAll(pageable);
			
			model.addAttribute("totalPages", productPage.getTotalPages());
			model.addAttribute("currentPage", page);
			model.addAttribute("size", size);
			
			model.addAttribute("products", productPage.getContent());
			model.addAttribute("message", "Create success");
			model.addAttribute("categorys", dao1.findAll());
			model.addAttribute("producers", producerRepo.findAll());
			
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", e);
		}

		return "/admin/admin";
	}

	@PostMapping("/admin/update")
	public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
            Model model, @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category_id") int categoryId,
            @RequestParam("producer_id") int producerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
		// Kiểm tra xem trường category và producer có được chọn hay không
	    if (categoryId == 0) {
	        bindingResult.rejectValue("category", "category.required", "Please select item type");
	    }
	    if (producerId == 0) {
	        bindingResult.rejectValue("producer", "producer.required", "Please select producer");
	    }

		try {
			Product existingProduct = dao.findById(product.getProductId()).orElse(null);
			if (existingProduct != null) {
				// Cập nhật các trường thông tin của sản phẩm
				existingProduct.setProductName(product.getProductName());
				existingProduct.setPrice(product.getPrice());
				existingProduct.setQuantity(product.getQuantity());
				existingProduct.setDiscount(product.getDiscount());

				// Cập nhật danh mục và nhà sản xuất
				Category category = dao1.findById(categoryId).orElse(null);
				Producer producer = producerRepo.findById(producerId).orElse(null);
				existingProduct.setCategory(category);
				existingProduct.setProducer(producer);
				existingProduct.setActive(true);
				if (!imageFile.isEmpty()) {
					// Xử lý file hình ảnh nếu có thay đổi
					String fileName = imageFile.getOriginalFilename();
					String uploadDir = "C:/uploads";
					File directory = new File(uploadDir);
					if (!directory.exists()) {
						directory.mkdirs();
					}
					String filePath = uploadDir + "/" + fileName;
					File dest = new File(filePath);
					imageFile.transferTo(dest);

					// Cập nhật đường dẫn hình ảnh
					existingProduct.setImage(filePath);
				}
				if (bindingResult.getErrorCount() > 1) {
					
					// Cập nhật danh sách sản phẩm theo trang hiện tại
					Pageable pageable = PageRequest.of(page, size);
					Page<Product> productPage = dao.findAll(pageable);
					
					model.addAttribute("totalPages", productPage.getTotalPages());
					model.addAttribute("currentPage", page);
					model.addAttribute("size", size);
					
					model.addAttribute("products", productPage.getContent());
					model.addAttribute("categorys", dao1.findAll());
					model.addAttribute("producers", producerRepo.findAll());
					
					return "/admin/admin";
				}
				dao.save(existingProduct);

				model.addAttribute("message", "Update success");
			} else {
				model.addAttribute("error", "Product not found");
			}

			// Cập nhật danh sách sản phẩm theo trang hiện tại
			Pageable pageable = PageRequest.of(page, size);
			Page<Product> productPage = dao.findAll(pageable);
			model.addAttribute("products", productPage.getContent());
			model.addAttribute("totalPages", productPage.getTotalPages());
			model.addAttribute("currentPage", page);
			model.addAttribute("size", size);

			model.addAttribute("categorys", dao1.findAll());
			model.addAttribute("producers", producerRepo.findAll());

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", e);
		}

		return "/admin/admin";
	}

	@PostMapping("/admin/delete")
	public String deleteProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
            Model model, @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category_id") int categoryId,
            @RequestParam("producer_id") int producerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
		if (bindingResult.hasErrors()) {
			// Xử lý khi dữ liệu không hợp lệ
			// return "update-product";
		}

		try {
			Product existingProduct = dao.findById(product.getProductId()).orElse(null);
			if (existingProduct != null) {
				// Cập nhật các trường thông tin của sản phẩm
				existingProduct.setProductName(product.getProductName());
				existingProduct.setPrice(product.getPrice());
				existingProduct.setQuantity(product.getQuantity());
				existingProduct.setDiscount(product.getDiscount());

				// Cập nhật danh mục và nhà sản xuất
				Category category = dao1.findById(categoryId).orElse(null);
				Producer producer = producerRepo.findById(producerId).orElse(null);
				existingProduct.setCategory(category);
				existingProduct.setProducer(producer);
				existingProduct.setActive(false);
				if (!imageFile.isEmpty()) {
					// Xử lý file hình ảnh nếu có thay đổi
					String fileName = imageFile.getOriginalFilename();
					String uploadDir = "C:/uploads";
					File directory = new File(uploadDir);
					if (!directory.exists()) {
						directory.mkdirs();
					}
					String filePath = uploadDir + "/" + fileName;
					File dest = new File(filePath);
					imageFile.transferTo(dest);

					// Cập nhật đường dẫn hình ảnh
					existingProduct.setImage(filePath);
				}

				dao.save(existingProduct);
				model.addAttribute("message", "Delete success");
				
			} else {
				model.addAttribute("error", "Product not found");
			}

			

			// Cập nhật danh sách sản phẩm theo trang hiện tại
			Pageable pageable = PageRequest.of(page, size);
			Page<Product> productPage = dao.findAll(pageable);
			model.addAttribute("products", productPage.getContent());
			model.addAttribute("totalPages", productPage.getTotalPages());
			model.addAttribute("currentPage", page);
			model.addAttribute("size", size);

			model.addAttribute("categorys", dao1.findAll());
			model.addAttribute("producers", producerRepo.findAll());
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", e);
		}

		return "/admin/admin";
	}

	// @PostMapping("/admin/delete")
	// public String delete(@RequestParam("productId") int productId,Model model) {
	// dao.deleteById(productId);
	// model.addAttribute("message", "Delete Success!");
	// // Xử lý sau khi xóa sản phẩm
	// return "redirect:/admin";
	// }

	@PostMapping("/admin/reset")
	public String reset(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);
		List<Product> products = dao.findAll();
		List<Category> categories = dao1.findAll();
		List<Producer> producers = producerRepo.findAll();
		model.addAttribute("products", products);
		model.addAttribute("categorys", categories);
		model.addAttribute("producers", producers);

		return "/admin/admin"; // Trả
	}

}
