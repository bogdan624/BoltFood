package com.bolt.services;

import com.bolt.model.Order;
import com.bolt.model.OrderItem;
import com.bolt.model.Produs;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.repository.IplayerRepository;
import com.bolt.repository.OrderRepository;
import com.bolt.repository.ProdusRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    public enum OrderStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELED
    }
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProdusRepository produsRepository;

    @Autowired
    private IplayerRepository playerRepository;

    @Override
    @Transactional
    public Order createOrder(Long playerId, List<Long> productIds, List<Integer> quantities) {
        var player = playerRepository.findById(playerId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Invalid player ID"));

        Order order = new Order();
        order.setPlayer(player);
        order.setDate(LocalDate.now());
        order.setStatus(Order.OrderStatus.PROCESSING);

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (int i = 0; i < productIds.size(); i++) {
            var product = produsRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
            OrderItem item = new OrderItem();
            item.setProdus(product);
            item.setQuantity(quantities.get(i));
            item.setPrice(product.getPret()); // Prețul unitar
            order.addItem(item); // Asociază produsul cu comanda
            total += product.getPret() * quantities.get(i);
        }

        order.setTotal(total);

        // Salvarea comenzii
        return orderRepository.save(order);
    }
    public void deleteOrdersByPlayerId(int playerId) {
        orderRepository.deleteByPlayerId(playerId);
    }


    public List<Order> getOrdersByPlayerId(Integer playerId) {
        return orderRepository.findByPlayerId(playerId);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByPlayerId(userId.intValue());
    }

}
