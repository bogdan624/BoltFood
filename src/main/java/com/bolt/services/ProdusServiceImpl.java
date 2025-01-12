package com.bolt.services;

import com.bolt.model.Produs;
import com.bolt.repository.ProdusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdusServiceImpl implements ProdusService {

    @Autowired
    private ProdusRepository produsRepository;

    @Override
    public List<Produs> findByRestaurantId(Long restaurantId) {
        return produsRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public Produs findById(Long id) {
        return produsRepository.findById(id).orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit!"));
    }

    @Override
    public Produs saveProdus(Produs produs) {
        return produsRepository.save(produs);
    }

    @Override
    public void deleteProdus(Long id) {
        produsRepository.deleteById(id);
    }
}
