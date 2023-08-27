package com.poly.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    
    @NotBlank(message = "{NotBlank.user.username}")
    @Column(name = "username")
    private String username;
    
    @NotBlank(message = "{NotBlank.user.password}")
    @Column(name = "password")
    private String password;
    
    @NotBlank(message = "{NotBlank.user.email}")
    @Email(message = "{Email.user.email}")
    @Column(name = "Email")
    private String email;
    
    @NotNull(message = "{NotNull.user.role}")
    @Column(name = "role")
    private boolean role;
    
    @Column(name = "isative")
    private boolean isative;
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    
    @OneToMany(mappedBy = "user")
    private List<Cart> carts;
    
    // Constructors, getters, setters, and other properties
}
