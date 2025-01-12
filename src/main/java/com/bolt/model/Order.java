package com.bolt.model;

import com.bolt.model.entity.PlayerMySQL;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerMySQL player; // Conectează comanda cu un PlayerMySQL

    private LocalDate date;
    private Double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // Enum pentru status

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Getteri și setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayerMySQL getPlayer() {
        return player;
    }

    public void setPlayer(PlayerMySQL player) {
        this.player = player;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Funcție pentru adăugarea unui produs în comandă
    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this); // Asociază produsul cu comanda curentă
    }
}
