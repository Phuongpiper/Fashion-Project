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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory")
public class Inventory implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private int inventoryId;
    
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @NotNull(message = "NotNull.inventory.quantity")
    @Positive(message = "Quantity must be positive")
    @Column(name = "quantity")
    private Integer quantity;
    
    @NotBlank(message = "NotBlank.inventory.address")
    @Column(name = "address")
    private String address;
    
    @Column(name = "active")
    private boolean active;
    // Constructors, getters, setters, and other properties
}
