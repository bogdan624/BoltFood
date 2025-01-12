package com.bolt.services;

import com.bolt.controller.auth.RegisterRequest;
import com.bolt.model.entity.PlayerMySQL;
import com.bolt.exceptions.customExceptions.UserNotFoundException;
import com.bolt.model.dto.PlayerGameDTO;

import java.util.List;

public interface IPlayerGamerService {

    List<PlayerGameDTO> getAllPlayersDTO();
    PlayerGameDTO updatePlayer(RegisterRequest updatedPlayer, int id);
    PlayerGameDTO findPlayerDTOById(int id);
    List<PlayerMySQL> getAllPlayerMySQL();
    PlayerMySQL getPlayer(int id);
    void deletePlayerById(int id);

}
