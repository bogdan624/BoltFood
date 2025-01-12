package com.bolt.services;

import com.bolt.controller.auth.RegisterRequest;
import com.bolt.exceptions.MessageException;
import com.bolt.exceptions.customExceptions.EmptyDataBaseException;
import com.bolt.exceptions.customExceptions.UserNotFoundException;
import com.bolt.model.dto.PlayerGameDTO;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.repository.IplayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerGamerServiceImpl implements IPlayerGamerService {

    private IplayerRepository playerRepositorySQL;
    private AuthenticationServiceImpl authenticationMySQLService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerGamerServiceImpl(
            IplayerRepository playerRepositorySQL,
            AuthenticationServiceImpl authenticationMySQLService,
            PasswordEncoder passwordEncoder) {
        this.playerRepositorySQL = playerRepositorySQL;
        this.authenticationMySQLService = authenticationMySQLService;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetAccountBalanceByPlayerId(int playerId) {
        PlayerMySQL player = playerRepositorySQL.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        player.setAccountBalance(0.0);
        playerRepositorySQL.save(player);
    }

    /**
     * 🔁 ----------  MAPPERS -----------
     */
    public PlayerGameDTO playerDTOfromPlayer(PlayerMySQL player) {
        return new PlayerGameDTO(player.getId(), player.getName(), player.getAmountOfOrders(), player.getAccountBalance());
    }

    /**
     *  ℹ️    ------- METHODS ----------------
     */

    @Override
    public List<PlayerGameDTO> getAllPlayersDTO() {
        List<PlayerMySQL> playerMySQLList = playerRepositorySQL.findAll();
        if (!playerMySQLList.isEmpty()) {
            return playerMySQLList.stream()
                    .map(this::playerDTOfromPlayer)
                    .collect(Collectors.toList());
        } else {
            log.error(MessageException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(MessageException.EMPTY_DATABASE);
        }
    }

    public PlayerMySQL getPlayer(int id) {
        return playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
    }


    @Override
    public PlayerGameDTO updatePlayer(RegisterRequest updatedPlayer, int id) {
        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // Check the new name is not duplicated
        String currentName = player.getName();
        String updatedName = updatedPlayer.getFirstname();
        if (!currentName.equalsIgnoreCase(updatedName)) {
            authenticationMySQLService.checkDuplicatedName(updatedName);
            player.setName(updatedName);
            playerRepositorySQL.save(player);
        }

        // Check the new email is not duplicated
        String currentEmail = player.getEmail();
        String updatedEmail = updatedPlayer.getEmail();
        if (!currentEmail.equalsIgnoreCase(updatedEmail)) {
            authenticationMySQLService.checkDuplicatedEmail(updatedEmail);
            player.setEmail(updatedEmail);
            playerRepositorySQL.save(player);
            log.warn("Log out and log in again, otherwise the token will fail because the username won't match");
        }

        // Set the new values
        player.setSurname(updatedPlayer.getLastname());
        player.setPassword(passwordEncoder.encode(updatedPlayer.getPassword()));
        playerRepositorySQL.save(player);

        return this.playerDTOfromPlayer(player);
    }

    @Override
    public PlayerGameDTO findPlayerDTOById(int id) {
        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
        return this.playerDTOfromPlayer(player);
    }






    @Override
    public List<PlayerMySQL> getAllPlayerMySQL() {
        return playerRepositorySQL.findAll();
    }

    @Override
    public void deletePlayerById(int id) {
        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
        playerRepositorySQL.deleteById(id);
        log.info("Player with ID " + id + " was deleted.");
    }

    public void addOrderToPlayer(int id) {
        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
        player.addOrder();
        playerRepositorySQL.save(player);
    }

    public void updateAccountBalance(int id, double amount) {
        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
        player.updateAccountBalance(amount);
        playerRepositorySQL.save(player);
    }

    public List<PlayerGameDTO> getAllPlayersByBalance() {
        return playerRepositorySQL.findAll().stream()
                .sorted(Comparator.comparingDouble(PlayerMySQL::getAccountBalance).reversed())
                .map(this::playerDTOfromPlayer)
                .collect(Collectors.toList());
    }

    public void updatePlayer(PlayerMySQL player) {
        playerRepositorySQL.save(player); // Salvează modificările în baza de date
    }

    public PlayerMySQL getPlayerById(int id) {
        return playerRepositorySQL.findById(id).orElse(null);  // Returnează jucătorul sau null dacă nu există
    }

    public PlayerMySQL findPlayerByEmail(String email) {
        return playerRepositorySQL.findByEmail(email).orElse(null);
    }
}
