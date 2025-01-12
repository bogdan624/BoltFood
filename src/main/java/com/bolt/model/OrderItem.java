package com.bolt.model;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produs_id", nullable = false)
    private Produs produs;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Integer quantity;

    private Double price; // Preț unitar la momentul comenzii

    // Getters și Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalPrice() {
        return quantity * price;
    }

    public void setProduct(Produs produs) {
        this.produs = produs;
    }

}
