package com.bolt.repository;


import com.bolt.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
        @Query(value = "SELECT * FROM boltfood.restaurant ORDER BY RAND() LIMIT 3", nativeQuery = true)
        List<Restaurant> getRandom3Restaurants();

}