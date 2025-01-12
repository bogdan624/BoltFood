package com.bolt.controller;

import com.bolt.controller.auth.RegisterRequest;
import com.bolt.exceptions.customExceptions.DuplicateUserEmailException;
import com.bolt.exceptions.customExceptions.DuplicateUserNameException;
import com.bolt.model.Restaurant;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.repository.IRestaurantService;
import com.bolt.repository.IplayerRepository;
import com.bolt.services.AuthenticationServiceImpl;
import com.bolt.services.IAuthenticationService;
import com.bolt.services.OrderServiceImpl;
import com.bolt.services.PlayerGamerServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
//@RequestMapping("/page")
public class pageController {

    private PlayerGamerServiceImpl services;
    private IAuthenticationService authService;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private IplayerRepository playerSQL;

    @Autowired
    public pageController(PlayerGamerServiceImpl services, AuthenticationServiceImpl authService) {
        this.services = services;
        this.authService = authService;
    }
    @Autowired
    private IRestaurantService restaurantService;



    @GetMapping({"/", "/home"})
    public String homePage(Model model, Authentication authentication) {
        List<Restaurant> randomRestaurants = restaurantService.getRandom3Restaurants();
        model.addAttribute("randomRestaurants", randomRestaurants); // Adăugăm în model
        model.addAttribute("title", "We are IT-Bolt Food");
        return "home";
    }

    @GetMapping("/account")
    public String accountPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirecționează la login dacă utilizatorul nu este autentificat
        }

        // Obține email-ul utilizatorului curent
        String email = principal.getName();

        // Găsește utilizatorul în baza de date
        PlayerMySQL player = services.findPlayerByEmail(email);
        if (player == null) {
            return "error/404"; // Returnează o pagină de eroare dacă utilizatorul nu este găsit
        }

        // Adaugă informațiile utilizatorului în model
        model.addAttribute("title", "Welcome to your account.");
        model.addAttribute("player", player);

        return "account";
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login page");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("title", "Login page");
        return "login";
    }



    @GetMapping("/register")
    public String register(Model model) {
        RegisterRequest registerRequest = new RegisterRequest();
        model.addAttribute("registerRequest", registerRequest);
        model.addAttribute("title", "Register page");
        return "register";
    }


    @PostMapping("/actionRegister")
    public String actionRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult result, Model model)
    {
        model.addAttribute("title", "Register page new");

        try{
            authService.register(registerRequest);
        }catch (DuplicateUserEmailException e){
            return "redirect:/register?duplicatedEmail=true";
        }catch (DuplicateUserNameException e){
            return "redirect:/register?duplicatedName=true";
        }
        return "redirect:/login?registrado=true";
    }


    @GetMapping("/adminArea")
    public String adminArea(Model model){
        model.addAttribute("title", "Admin area: As an admin you have full control of all users ");

        List<PlayerMySQL> listPlayers = services.getAllPlayerMySQL();

        model.addAttribute("players", listPlayers);

        return "admin/home";
    }
    @ModelAttribute("currentUser")
    public PlayerMySQL populateCurrentUser() {
        // Obține autentificarea curentă
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifică dacă utilizatorul este autentificat
        if (authentication != null && authentication.getPrincipal() instanceof PlayerMySQL) {
            return (PlayerMySQL) authentication.getPrincipal();
        }

        return null; // Returnează null dacă nu este autentificat
    }

    @PostMapping("/add-funds")
    public String addFunds(@RequestParam("amount") double amount, Principal principal, Model model) {
        if (amount <= 0) {
            model.addAttribute("error", "Amount must be greater than zero.");
            return "redirect:/account?error=true"; // Redirecționează cu eroare
        }

        // Găsește utilizatorul curent
        String email = principal.getName();
        PlayerMySQL player = services.findPlayerByEmail(email);

        if (player == null) {
            model.addAttribute("error", "Player not found.");
            return "error/404"; // Returnează o pagină de eroare dacă utilizatorul nu este găsit
        }

        // Preia datele existente din baza de date pentru a actualiza doar câmpurile relevante
        PlayerMySQL existingPlayer = services.getPlayerById(player.getId());

        // Actualizează balanța contului adăugând suma specificată
        double newBalance = player.getAccountBalance() + amount;

        // Setează noua balanță
        player.setAccountBalance(newBalance);

        // Asigură-te că nu se schimbă alte câmpuri care nu au fost modificate
        if (player.getPassword() == null || player.getPassword().isEmpty()) {
            player.setPassword(existingPlayer.getPassword());
        }
        if (player.getAmountOfOrders() == 0) {
            player.setAmountOfOrders(existingPlayer.getAmountOfOrders());
        }
        if (player.getRegisterDate() == null) {
            player.setRegisterDate(existingPlayer.getRegisterDate());
        }

        // Actualizează utilizatorul în baza de date
        try {
            services.updatePlayer(player);
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while updating the account.");
            return "redirect:account?error=true"; // Returnează eroare în caz de eșec
        }

        // Actualizează contextul de securitate
        updateSecurityContext(player, principal);

        return "redirect:account"; // Pagina de cont cu succes
    }



    @GetMapping("/clear/{id}")
    public String clearById(@PathVariable int id) {
        if (id > 0) {
            // Șterge comenzile asociate jucătorului
            orderService.deleteOrdersByPlayerId(id);

            // Resetează balanța contului
            services.resetAccountBalanceByPlayerId(id);

            playerSQL.updatePlayerOrderCount();

            log.info("Cleared orders and reset account balance for player ID: " + id);
            return "redirect:/adminArea";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/delete-player/{id}")
    public String deletePlayerById(@PathVariable int id) {
        if (id > 0) {
            services.deletePlayerById(id);
            log.info("Deleted player ID: " + id + " and all their data.");
            return "redirect:/adminArea";
        } else {
            return "error/404";
        }
    }
    @GetMapping("/edit-player")
    public String editCurrentPlayer(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login"; // Redirecționează la login dacă utilizatorul nu este autentificat
        }

        // Obține email-ul utilizatorului curent din obiectul Principal
        String email = principal.getName();

        // Caută utilizatorul în baza de date folosind email-ul
        PlayerMySQL player = services.findPlayerByEmail(email);
        if (player == null) {
            return "error/404"; // Returnează o pagină de eroare dacă utilizatorul nu este găsit
        }

        // Adaugă informațiile despre utilizator în model
        model.addAttribute("title", "Edit Your Profile");
        model.addAttribute("player", player);

        // Returnează către pagina de editare
        return "editPlayer";
    }

    @GetMapping("/edit-player/{id}")
    public String editPlayer(@PathVariable int id, Model model) {
        PlayerMySQL player = services.getPlayerById(id);
        if (player == null) {
            return "error/404";
        }
        model.addAttribute("title", "Edit Player");
        model.addAttribute("player", player);
        return "editPlayer";
    }

    @PostMapping("/update")
    public String updatePlayer(@Valid @ModelAttribute("player") PlayerMySQL player,
                               BindingResult result,
                               Model model,
                               @RequestParam(value = "source", required = false) String source,
                               Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Edit Player");
            return "editPlayer";
        }

        // Preia datele existente din baza de date
        PlayerMySQL existingPlayer = services.getPlayerById(player.getId());

        // Actualizează doar câmpurile care au valori noi
        if (player.getPassword() == null || player.getPassword().isEmpty()) {
            player.setPassword(existingPlayer.getPassword());
        }
        if (player.getAccountBalance() == 0) {
            player.setAccountBalance(existingPlayer.getAccountBalance());
        }
        if (player.getAmountOfOrders() == 0) {
            player.setAmountOfOrders(existingPlayer.getAmountOfOrders());
        }
        if (player.getRegisterDate() == null) {
            player.setRegisterDate(existingPlayer.getRegisterDate());
        }

        // Actualizează utilizatorul în baza de date
        services.updatePlayer(player);
        log.info("Updated player with ID: " + player.getId());

        // Actualizează contextul de securitate
        updateSecurityContext(player, principal);

        // Verifică sursa și redirecționează corespunzător
        if ("account".equalsIgnoreCase(source)) {
            return "redirect:/account";
        } else if ("adminArea".equalsIgnoreCase(source)) {
            return "redirect:/adminArea";
        } else {
            return "redirect:/home"; // Redirecționare implicită în caz că sursa nu este specificată
        }
    }
    private void updateSecurityContext(PlayerMySQL player, Principal principal) {
        // Obține autentificarea curentă
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Actualizează obiectul principal cu noile informații
        PlayerMySQL updatedPlayer = services.findPlayerByEmail(player.getEmail());

        // Creează un nou Authentication cu datele actualizate
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedPlayer, // Obiectul principal (actualizat)
                authentication.getCredentials(),
                authentication.getAuthorities() // Menține autoritățile existente
        );

        // Setează noul Authentication în contextul de securitate
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        log.info("Security context updated for user: " + updatedPlayer.getEmail());
    }


    private String existingRegisterDateFromDatabase(int playerId) {
        return services.getPlayerById(playerId).getRegisterDate();
    }
    @GetMapping("/resetPassword")
    public String showResetPasswordForm(Model model, Principal principal) {
        if (principal != null) {
            // Obține utilizatorul logat
            String loggedInEmail = principal.getName(); // În mod normal, numele principalului este emailul
            model.addAttribute("email", loggedInEmail);
            model.addAttribute("isLoggedIn", true); // Adaugă un flag pentru a marca utilizatorul ca logat
        } else {
            model.addAttribute("email", ""); // Lasă câmpul gol dacă utilizatorul nu e logat
            model.addAttribute("isLoggedIn", false); // Flag fals dacă nu este logat
        }
        return "resetPassword"; // Afișează pagina de resetare a parolei
    }


    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model,
                                Principal principal) {
        // Determină dacă utilizatorul este logat
        boolean isLoggedIn = (principal != null);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            // Completează emailul utilizatorului logat
            email = principal.getName();
            model.addAttribute("email", email);

            // Găsește utilizatorul după email
            PlayerMySQL player = services.findPlayerByEmail(email);
            if (player == null) {
                model.addAttribute("error", "User does not exist!");
                return "resetPassword"; // Reafișează pagina
            }

            // Verifică dacă parola veche este corectă
            if (!BCrypt.checkpw(oldPassword, player.getPassword())) {
                model.addAttribute("error", "Old password is incorrect!");
                return "resetPassword"; // Reafișează pagina
            }
        } else {
            // Caută utilizatorul după email
            PlayerMySQL player = services.findPlayerByEmail(email);
            if (player == null) {
                model.addAttribute("error", "Email does not exist!");
                return "resetPassword"; // Reafișează pagina
            }
        }

        // Verifică dacă noile parole coincid
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("email", email); // Menținem emailul în model
            model.addAttribute("error", "New passwords do not match!");
            return "resetPassword"; // Reafișează pagina
        }

        // Hash parola nouă (pentru securitate)
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        try {
            // Găsește utilizatorul și actualizează parola
            PlayerMySQL player = services.findPlayerByEmail(email);
            player.setPassword(hashedPassword);
            services.updatePlayer(player);

            model.addAttribute("success", "Password reset successfully!");

            // Dacă utilizatorul este logat, redirecționează
            if (isLoggedIn) {
                return "redirect:/account"; // Redirecționează către cont
            }

            return "login"; // Redirecționează la login
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while resetting password.");
            return "resetPassword"; // Reafișează pagina
        }
    }
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        // Validarea adresei de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body("Subscription successful. Thank you for subscribing!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
        }
    }

}
