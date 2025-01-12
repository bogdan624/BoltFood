package com.bolt.controller;

import com.bolt.model.Restaurant;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository; // Repository-ul pentru accesarea datelor despre restaurante

    /**
     * Endpoint pentru afișarea listei de restaurante.
     * @param model Obiectul model pentru a transfera datele către vizualizare.
     * @return Pagina HTML pentru lista de restaurante.
     */
    @GetMapping
    public String listaRestaurante(Model model) {
        List<Restaurant> restaurant = restaurantRepository.findAll(); // Preia toate restaurantele
        model.addAttribute("restaurant", restaurant); // Adaugă lista în model
        return "restaurant"; // Returnează numele paginii HTML pentru lista de restaurante
    }

    /**
     * Endpoint pentru afișarea formularului de adăugare a unui restaurant.
     * @param model Obiectul model pentru a adăuga atribute necesare în șablon.
     * @return Pagina HTML pentru formularul de restaurant.
     */
    @GetMapping("/new")
    public String formularAdaugaRestaurant(Model model) {
        model.addAttribute("restaurant", new Restaurant()); // Creează un obiect gol pentru formular
        return "restaurant-form"; // Returnează numele paginii HTML pentru formular
    }

    /**
     * Endpoint pentru salvarea unui nou restaurant.
     * @param restaurant Obiectul restaurant ce urmează să fie salvat.
     * @return Redirecționare către lista de restaurante.
     */
    @PostMapping
    public String adaugaRestaurant(@ModelAttribute Restaurant restaurant) {
        restaurantRepository.save(restaurant); // Salvează restaurantul în baza de date
        return "redirect:/restaurant"; // Redirecționează la lista de restaurante
    }

    /**
     * Endpoint pentru afișarea formularului de editare a unui restaurant existent.
     * @param id ID-ul restaurantului de editat.
     * @param model Model pentru transferul datelor către vizualizare.
     * @return Pagina HTML pentru formularul de editare.
     */
    @GetMapping("/edit/{id}")
    public String formularEditeazaRestaurant(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id invalid: " + id)); // Găsește restaurantul
        model.addAttribute("restaurant", restaurant); // Adaugă restaurantul în model
        return "restaurant-form"; // Returnează numele paginii HTML pentru formular
    }

    /**
     * Endpoint pentru actualizarea unui restaurant existent.
     * @param id ID-ul restaurantului de actualizat.
     * @param restaurant Obiectul restaurant cu noile date.
     * @return Redirecționare către lista de restaurante.
     */
    @PostMapping("/update/{id}")
    public String actualizeazaRestaurant(@PathVariable Long id, @ModelAttribute Restaurant restaurant) {
        // Preia restaurantul existent pentru a păstra datele care nu trebuie suprascrise
        Restaurant restaurantExistent = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id invalid: " + id));

        // Menține lista de produse existentă și asociază ID-ul
        restaurant.setProduse(restaurantExistent.getProduse());
        restaurant.setId(id);

        // Salvează restaurantul actualizat
        restaurantRepository.save(restaurant);
        return "redirect:/restaurant"; // Redirecționează la lista de restaurante
    }

    /**
     * Endpoint pentru ștergerea unui restaurant.
     * @param id ID-ul restaurantului ce trebuie șters.
     * @return Redirecționare către lista de restaurante.
     */
    @GetMapping("/delete/{id}")
    public String stergeRestaurant(@PathVariable Long id) {
        restaurantRepository.deleteById(id); // Șterge restaurantul din baza de date
        return "redirect:/restaurant"; // Redirecționează la lista de restaurante
    }

    /**
     * Endpoint pentru afișarea produselor asociate unui restaurant.
     * @param id ID-ul restaurantului.
     * @param model Model pentru transferul datelor către vizualizare.
     * @return Pagina HTML pentru afișarea produselor.
     */
    @GetMapping("/{id}/produse")
    public String getProduseByRestaurantId(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id invalid: " + id));
        model.addAttribute("restaurant", restaurant); // Adaugă restaurantul în model
        model.addAttribute("produse", restaurant.getProduse()); // Adaugă produsele asociate
        return "produse"; // Returnează pagina HTML pentru produsele asociate
    }

    /**
     * Endpoint pentru afișarea unor restaurante selectate aleatoriu.
     * @param model Model pentru transferul datelor către vizualizare.
     * @return Pagina HTML pentru afișarea restaurantelor.
     */
    @GetMapping("/restaurants/random")
    public String getRandomRestaurants(Model model) {
        List<Restaurant> randomRestaurants = restaurantRepository.getRandom3Restaurants(); // Preia 3 restaurante random
        model.addAttribute("randomRestaurants", randomRestaurants); // Adaugă restaurantele în model
        return "home"; // Returnează pagina unde vor fi afișate
    }

    /**
     * Adaugă utilizatorul autentificat în model pentru a fi disponibil în toate metodele.
     * @return Obiectul utilizatorului autentificat sau null dacă nu există autentificare.
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
