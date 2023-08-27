package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Dao.ProductDAO;
import com.example.demo.Model.Product;
import com.example.demo.Model.Report;
import com.example.demo.service.SessionService;

import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private final ProductDAO dao;

	@Autowired
	private final SessionService session;

	public ProductController(ProductDAO dao, SessionService session) {
		this.dao = dao;
		this.session = session;
	}

	@RequestMapping("/search")
	public String search(Model model, @RequestParam("min") Optional<Double> min,
			@RequestParam("max") Optional<Double> max) {
		double minPrice = min.orElse(Double.MIN_VALUE);
		double maxPrice = max.orElse(Double.MAX_VALUE);
		List<Product> items = dao.findByPriceBetween(minPrice, maxPrice);
		model.addAttribute("items", items);
		return "product/search";
	}

	@RequestMapping("/search-and-page")
	public String searchAndPage(Model model, @RequestParam("keywords") Optional<String> kw,
			@RequestParam("p") Optional<Integer> p) {
		String kwords = kw.orElse((String) session.get("keywords", ""));
		session.set("keywords", kwords);
		Pageable pageable = PageRequest.of(p.orElse(0), 5);
		Page<Product> page = dao.findAllByNameLike("%" + kwords + "%", pageable);
		model.addAttribute("page", page);
		return "product/search-and-page";
	}
	
	@RequestMapping("/report/inventory-by-category")
	public String inventory(Model model) {
	    List<Report> items = dao.getInventoryByCategory();
	    model.addAttribute("items", items);
	    return "product/inventory-by-category";
	}
}
