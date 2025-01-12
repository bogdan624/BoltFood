package com.bolt.services;


import com.bolt.model.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Long playerId, List<Long> productIds, List<Integer> quantities);
    List<Order> getOrdersByUserId(Long playerId);
}
