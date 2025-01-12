package com.bolt.repository;


import com.bolt.model.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPlayerId(Integer playerId); // Utilizează `player.id`
    @Modifying
    @Transactional
    void deleteByPlayerId(int playerId);

}
