package com.poly.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Data
@Getter
@Setter
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;
    
    @NotBlank(message = "NotBlank.product.productName")
    @Column(name = "product_name")
    private String productName;
    
    
    @Column(name = "price")
    @NotNull(message = "NotNull.product.price")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
   @NotNull(message = "NotNull.product.quantity")
   @Positive(message = "Price must be positive")
    @Column(name = "quantity")
    private Integer  quantity;
    
   @NotNull(message = "NotNull.product.discount")
   @Positive(message = "discount must be positive")
    @Column(name = "discount")
    private BigDecimal discount;
    
   
    @Column(name = "image")
    private String image;
    
    @Column(name = "active")
    private boolean active;
    

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producer;
    
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
    
    @OneToMany(mappedBy = "product")
    private List<CartProduct> cartProducts;
    
    @OneToOne(mappedBy = "product")
    private Inventory inventory;
    
    @Override
    public String toString() {
        return "Product[id=" + productId + ", name=" + productName + "]";
    }


    // Constructors, getters, setters, and other properties
}