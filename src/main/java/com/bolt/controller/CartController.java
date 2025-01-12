package com.bolt.controller;

import com.bolt.model.Produs;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.services.ProdusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pentru gestionarea operațiunilor asupra coșului de cumpărături.
 */
@Controller
@RequestMapping("/cart")
@SessionAttributes("cartItems")
public class CartController {

    @Autowired
    private ProdusService produsService;

    /**
     * Inițializează lista de produse din coș.
     *
     * @return o listă goală de produse
     */
    @ModelAttribute("cartItems")
    public List<Produs> initializeCart() {
        return new ArrayList<>();
    }

    /**
     * Adaugă un produs în coș pe baza ID-ului acestuia.
     *
     * @param produsId ID-ul produsului de adăugat
     * @param cartItems Lista curentă de produse din coș
     * @param model Modelul pentru transmiterea datelor în view
     * @return redirecționare către pagina de vizualizare a coșului
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam("produsId") Long produsId,
                            @ModelAttribute("cartItems") List<Produs> cartItems,
                            Model model) {
        Produs produs = produsService.findById(produsId);
        if (produs != null) {
            cartItems.add(produs);
            model.addAttribute("cartItems", cartItems);
        } else {
            model.addAttribute("error", "Produsul nu a fost găsit.");
        }
        return "redirect:/cart/view";
    }

    /**
     * Afișează conținutul coșului de cumpărături.
     *
     * @param cartItems Lista curentă de produse din coș
     * @param model Modelul pentru transmiterea datelor în view
     * @return pagina de vizualizare a coșului
     */
    @GetMapping("/view")
    public String viewCart(@ModelAttribute("cartItems") List<Produs> cartItems, Model model) {
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartItems.stream().mapToDouble(Produs::getPret).sum());
        return "cart";
    }

    /**
     * Șterge un produs din coș pe baza ID-ului acestuia.
     *
     * @param produsId ID-ul produsului de șters
     * @param cartItems Lista curentă de produse din coș
     * @return redirecționare către pagina de vizualizare a coșului
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("produsId") Long produsId,
                                 @ModelAttribute("cartItems") List<Produs> cartItems) {
        cartItems.removeIf(produs -> produs.getId().equals(produsId));
        return "redirect:/cart/view";
    }

    /**
     * Golește complet coșul de cumpărături.
     *
     * @param cartItems Lista curentă de produse din coș
     * @return redirecționare către pagina de vizualizare a coșului
     */
    @PostMapping("/clear")
    public String clearCart(@ModelAttribute("cartItems") List<Produs> cartItems) {
        cartItems.clear();
        return "redirect:/cart/view";
    }

    /**
     * Procesează finalizarea comenzii (checkout).
     *
     * @param cartItems Lista curentă de produse din coș
     * @param model Modelul pentru transmiterea datelor în view
     * @return pagina de confirmare a comenzii sau redirecționare dacă coșul este gol
     */
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("cartItems") List<Produs> cartItems, Model model) {
        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("error", "Coșul este gol. Adaugă produse înainte de a continua.");
            return "redirect:/cart/view";
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartItems.stream().mapToDouble(Produs::getPret).sum());
        return "checkout-confirmation";
    }

    /**
     * Populează informațiile despre utilizatorul curent, dacă este autentificat.
     *
     * @return obiectul PlayerMySQL al utilizatorului sau null dacă utilizatorul nu este autentificat
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
