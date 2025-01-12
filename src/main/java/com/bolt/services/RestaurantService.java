package com.bolt.services;

import com.bolt.model.Restaurant;
import com.bolt.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getRandom3Restaurants() {
        return restaurantRepository.getRandom3Restaurants();
    }
}
