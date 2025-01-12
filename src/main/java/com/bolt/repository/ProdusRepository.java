package com.bolt.repository;

import com.bolt.model.Produs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdusRepository extends JpaRepository<Produs, Long> {

    // Metodă pentru a găsi produsele asociate unui restaurant
    List<Produs> findByRestaurantId(Long restaurantId);
}
