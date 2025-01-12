package com.bolt.controller;

import com.bolt.model.Produs;
import com.bolt.model.Restaurant;
import com.bolt.model.RestaurantServiceImpl;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.services.ProdusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produs")
public class ProdusController {

    @Autowired
    private ProdusService produsService; // Service pentru gestionarea produselor

    @Autowired
    private RestaurantServiceImpl restaurantService; // Service pentru gestionarea restaurantelor

    /**
     * Endpoint pentru afișarea formularului de adăugare produs.
     * @param restaurantId ID-ul restaurantului asociat.
     * @param model Model pentru a adăuga atribute necesare în șablon.
     * @return Numele paginii HTML pentru formularul produsului.
     */
    @GetMapping("/new")
    public String showAddProdusForm(@RequestParam("restaurantId") Long restaurantId, Model model) {
        model.addAttribute("produs", new Produs()); // Creează un obiect gol pentru produs
        model.addAttribute("restaurantId", restaurantId); // Adaugă ID-ul restaurantului asociat
        return "produse-form"; // Returnează pagina produse-form.html
    }

    /**
     * Endpoint pentru salvarea unui produs nou.
     * @param produs Obiectul produs care urmează să fie salvat.
     * @param restaurantId ID-ul restaurantului asociat.
     * @return Redirecționare către lista produselor pentru restaurantul specificat.
     */
    @PostMapping("/save")
    public String saveProdus(@ModelAttribute Produs produs, @RequestParam("restaurantId") Long restaurantId) {
        Restaurant restaurant = restaurantService.findById(restaurantId); // Găsește restaurantul asociat
        produs.setRestaurant(restaurant); // Asociază produsul cu restaurantul
        produsService.saveProdus(produs); // Salvează produsul în baza de date
        return "redirect:/restaurant/" + restaurantId + "/produse"; // Redirecționează la lista produselor
    }

    /**
     * Endpoint pentru afișarea formularului de editare a unui produs.
     * @param id ID-ul produsului de editat.
     * @param model Model pentru a adăuga produsul existent și ID-ul restaurantului.
     * @param restaurantId ID-ul restaurantului asociat.
     * @return Numele paginii HTML pentru formularul produsului.
     */
    @GetMapping("/edit/{id}")
    public String showEditProdusForm(@PathVariable Long id, Model model, @RequestParam("restaurantId") Long restaurantId) {
        Produs produs = produsService.findById(id); // Găsește produsul după ID
        model.addAttribute("produs", produs); // Adaugă produsul în model
        model.addAttribute("restaurantId", restaurantId); // Adaugă ID-ul restaurantului în model
        return "produse-form"; // Returnează pagina produse-form.html
    }

    /**
     * Endpoint pentru actualizarea unui produs existent.
     * @param id ID-ul produsului de actualizat.
     * @param produs Obiectul produs cu datele actualizate.
     * @param model Model pentru a adăuga date în șablon (opțional).
     * @param restaurantId ID-ul restaurantului asociat (opțional).
     * @return Redirecționare către lista produselor pentru restaurantul specificat.
     */
    @PostMapping("/update/{id}")
    public String updateProdus(@PathVariable Long id, @ModelAttribute Produs produs, Model model, @RequestParam("restaurantId") Long restaurantId) {
        Produs existingProdus = produsService.findById(id); // Găsește produsul existent după ID
        model.addAttribute("restaurantId", restaurantId); // Adaugă ID-ul restaurantului în model

        // Actualizează câmpurile produsului existent
        existingProdus.setNume(produs.getNume());
        existingProdus.setDescriere(produs.getDescriere());
        existingProdus.setPret(produs.getPret());

        // Verifică și menține relația cu restaurantul asociat
        if (existingProdus.getRestaurant() == null) {
            throw new IllegalStateException("Produsul nu are un restaurant asociat!");
        }

        produsService.saveProdus(existingProdus); // Salvează modificările produsului
        return "redirect:/restaurant/" + existingProdus.getRestaurant().getId() + "/produse"; // Redirecționează la lista produselor
    }

    /**
     * Endpoint pentru ștergerea unui produs.
     * @param id ID-ul produsului de șters.
     * @return Redirecționare către lista produselor restaurantului asociat.
     */
    @GetMapping("/delete/{id}")
    public String deleteProdus(@PathVariable Long id) {
        Produs produs = produsService.findById(id); // Găsește produsul după ID
        Long restaurantId = produs.getRestaurant().getId(); // Obține ID-ul restaurantului asociat
        produsService.deleteProdus(id); // Șterge produsul
        return "redirect:/restaurant/" + restaurantId + "/produse"; // Redirecționează la lista produselor
    }

    /**
     * Atribuie utilizatorul curent pentru a fi accesibil în toate metodele.
     * @return Obiectul utilizatorului autentificat sau null dacă nu este autentificat.
     */
    @ModelAttribute("currentUser")
    public PlayerMySQL populateCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof PlayerMySQL) {
            return (PlayerMySQL) authentication.getPrincipal(); // Returnează utilizatorul autentificat
        }

        return null; // Returnează null dacă nu este autentificat
    }
}
