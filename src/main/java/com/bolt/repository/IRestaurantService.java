package com.bolt.repository;


import com.bolt.model.Restaurant;
import java.util.List;

public interface IRestaurantService {
    List<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(Long id);
    Restaurant saveRestaurant(Restaurant restaurant);
    void deleteRestaurant(Long id);

    Restaurant findById(Long id);

    List<Restaurant> getRandom3Restaurants();
}
