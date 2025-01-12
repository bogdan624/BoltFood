package com.bolt.controller;

import com.bolt.model.Order;
import com.bolt.model.Produs;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.repository.IplayerRepository;
import com.bolt.services.CartService;
import com.bolt.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pentru gestionarea comenzilor utilizatorului.
 */
@Controller
@RequestMapping("/orders")
@SessionAttributes("cartItems")
public class OrderController {

    @Autowired
    private IplayerRepository playerRepositorySQL;

    private CartService cartService;

    @Autowired
    private OrderService orderService;

    /**
     * Constructor pentru injectarea serviciului de coș.
     *
     * @param cartService Serviciul de gestionare a coșului
     */
    @Autowired
    public OrderController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Afișează istoricul comenzilor pentru utilizatorul logat.
     *
     * @param model Modelul pentru transmiterea datelor către view
     * @return Numele paginii HTML pentru afișarea comenzilor
     */
    @GetMapping
    public String getOrderHistory(Model model) {
        // Obține utilizatorul logat din contextul de securitate
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PlayerMySQL currentUser = (PlayerMySQL) authentication.getPrincipal();

        // Obține comenzile utilizatorului logat
        Integer userId = currentUser.getId();
        List<Order> orders = orderService.getOrdersByUserId(userId.longValue());
        model.addAttribute("orders", orders);

        return "orders"; // Pagina HTML pentru afișarea comenzilor
    }

    /**
     * Creează o comandă nouă pentru utilizatorul logat pe baza produselor din coș.
     *
     * @param cartItems Lista produselor din coș
     * @param model Modelul pentru transmiterea datelor către view
     * @return Redirecționare către pagina istoricului comenzilor sau pagina de vizualizare a coșului în caz de eroare
     */
    @PostMapping("/create")
    public String createOrder(@ModelAttribute("cartItems") List<Produs> cartItems, Model model) {
        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("error", "Coșul este gol. Nu se poate finaliza comanda.");
            return "redirect:/cart/view";
        }

        // Obține utilizatorul logat din contextul de securitate
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PlayerMySQL currentUser = (PlayerMySQL) authentication.getPrincipal();

        double total = cartItems.stream().mapToDouble(Produs::getPret).sum();

        // Verifică dacă utilizatorul are suficiente fonduri
        if (currentUser.getAccountBalance() < total) {
            return "redirect:/cart/view?error=fonduri_insuficiente"; // Redirecționare cu mesaj de eroare
        }

        // Pregătește datele pentru crearea comenzii
        List<Long> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (Produs produs : cartItems) {
            productIds.add(produs.getId());
            quantities.add(1); // Cantitate implicită
        }

        // Creează comanda
        orderService.createOrder(currentUser.getId().longValue(), productIds, quantities);

        // Actualizează balanța contului utilizatorului
        currentUser.setAccountBalance(currentUser.getAccountBalance() - total);
        playerRepositorySQL.save(currentUser);

        // Golește coșul de cumpărături
        cartItems.clear();

        // Actualizează numărul de comenzi pentru utilizator
        currentUser.addOrder();
        playerRepositorySQL.updatePlayerOrderCount();

        return "redirect:/orders"; // Redirecționare către pagina comenzilor
    }

    /**
     * Populează utilizatorul curent autentificat, dacă există.
     *
     * @return Obiectul utilizatorului curent sau null dacă nu este autentificat
     */
    @ModelAttribute("currentUser")
    public PlayerMySQL populateCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PlayerMySQL) {
            return (PlayerMySQL) authentication.getPrincipal();
        }
        return null;
    }
}
