package com.ashlesh.mycoolproject.entity;

import com.ashlesh.mycoolproject.enums.FoodCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "restaurant_id"}))
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // BigDecimal for money: never use double/float
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // STRING stores "MAIN_COURSE" in DB (ORDINAL would store 1,2,3 and break if enum order changes)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FoodCategory category;

    @Column(nullable = false)
    private boolean available = true;

    // Many food items belong to one restaurant. LAZY = restaurant is loaded only when needed.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public FoodItem() {
    }

    @PrePersist
    public void setCreationTime() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpdateTime() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}