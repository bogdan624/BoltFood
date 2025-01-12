package com.bolt.model.entity;

import com.bolt.model.RestaurantServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entity Player Information")
@Table(name = "Player")
public class PlayerMySQL implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(defaultValue = "PlayerID", description = "Here goes the player's ID")
    @Column(unique = true)
    private Integer id;

    @Column(nullable = false)
    @Schema(defaultValue = "PlayerName", description = "Here goes the player's name")
    @Size(min = 3, max = 20)
    private String name;

    @Column(nullable = false)
    private String registerDate;

    private String surname;
    private String email;
    private String password;

    private int amountOfOrders = 0; // Nou: număr comenzi
    private double accountBalance = 0.0; // Nou: balanța contului

    @Enumerated(EnumType.STRING)
    private Role role;

    public PlayerMySQL(String name) {
        this.name = name;
        registerDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                .format(new java.util.Date());
    }

    public PlayerMySQL(Integer id, String name) {
        this.id = id;
        this.name = name;
        registerDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                .format(new java.util.Date());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addOrder() {
        this.amountOfOrders++;
    }

    public void updateAccountBalance(double amount) {
        this.accountBalance += amount;
    }

    public void resetAccountData() {
        this.amountOfOrders = 0;
        this.accountBalance = 0.0;
    }

}
