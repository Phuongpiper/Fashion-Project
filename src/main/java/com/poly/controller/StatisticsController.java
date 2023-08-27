package com.poly.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.repository.OrderRepository;
import com.poly.repository.ProductRepository;

@Controller
public class StatisticsController {
    @Autowired
    ProductRepository productService;

    @GetMapping("/statistics")
    public String showStatisticsPage(Model model) {
        List<Object[]> categoryStats = productService.countProductsByCategory();
        List<Object[]> producerStats = productService.countProductsByProducer();
        
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("producerStats", producerStats);

        return "/admin/statistics";
    }
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/Revenue")
    public String showStatisticsPage1(Model model) {
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        return "/admin/Revenue";
    }

    @GetMapping("/Revenue/revenue-by-month")
    public String getRevenueByMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            Model model) {
        BigDecimal revenueByMonth = orderRepository.getRevenueByMonth(year, month);
        model.addAttribute("revenueByMonth", revenueByMonth != null ? revenueByMonth : BigDecimal.ZERO);
        return "/admin/Revenue";
    }

    @GetMapping("/Revenue/revenue-by-year")
    public String getRevenueByYear(
            @RequestParam("year") int year,
            Model model) {
        BigDecimal revenueByYear = orderRepository.getRevenueByYear(year);
        model.addAttribute("revenueByYear", revenueByYear != null ? revenueByYear : BigDecimal.ZERO);
        return "/admin/Revenue";
    }

    @GetMapping("/Revenue/revenue-by-date-range")
    public String getRevenueByDateRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Model model) {
        BigDecimal revenueByDateRange = orderRepository.getRevenueByDateRange(startDate, endDate);
        model.addAttribute("revenueByDateRange", revenueByDateRange != null ? revenueByDateRange : BigDecimal.ZERO);
        return "/admin/Revenue";
    }
}

