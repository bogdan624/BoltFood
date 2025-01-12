package com.bolt.repository;

import com.bolt.model.entity.PlayerMySQL;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IplayerRepository extends JpaRepository<PlayerMySQL, Integer> {
    boolean existsByEmail(String email);
    Optional<PlayerMySQL> findByEmail(String email);
    @Modifying
    @Transactional
    @Query(value = """
    UPDATE boltfood.player u
    SET u.amount_of_orders = (
        SELECT COUNT(*)
        FROM boltfood.orders o
        WHERE o.player_id = u.id
    )
""", nativeQuery = true)
    void updatePlayerOrderCount();


}
