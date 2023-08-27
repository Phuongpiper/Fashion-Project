package com.poly.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "producer")
public class Producer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id")
    private int producerId;
    
    @Column(name = "producer_name")
    private String producerName;
    
    @Column(name = "address")
    private String address;
    
    @OneToMany(mappedBy = "producer")
    private List<Product> products;
    
    // Constructors, getters, setters, and other properties
}