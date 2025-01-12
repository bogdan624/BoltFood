package com.bolt.services;

import com.bolt.model.Produs;

import java.util.List;

public interface ProdusService {

    List<Produs> findByRestaurantId(Long restaurantId);

    Produs findById(Long id);

    Produs saveProdus(Produs produs);

    void deleteProdus(Long id);
}
