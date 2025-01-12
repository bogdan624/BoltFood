package com.bolt.services;

import com.bolt.model.Produs;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private List<Produs> cartProducts = new ArrayList<>();

    public void addProductToCart(Long produsId) {
        // Caută produsul și adaugă-l în coș
        Produs produs = findProdusById(produsId); // Creează o metodă pentru a găsi produsul
        if (produs != null) {
            cartProducts.add(produs);
        }
    }

    public List<Produs> getCartProducts() {
        if (cartProducts == null) {
            return new ArrayList<>();
        }
        return cartProducts;
    }

    public void removeProductFromCart(Long produsId) {
        cartProducts.removeIf(produs -> produs.getId().equals(produsId));
    }

    private Produs findProdusById(Long produsId) {
        // Logică pentru a căuta produsul dintr-un repository sau listă
        return null; // Trebuie implementat
    }
    public void clearCart() {
        cartProducts.clear();
    }
}
