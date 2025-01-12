package com.bolt.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerGameDTO {
    @Schema(defaultValue = "1", description = "Player ID")
    private Integer id;

    @Schema(defaultValue = "Name", description = "Player Name")
    private String name;

    public PlayerGameDTO(Integer id, String name, int amountOfOrders, double accountBalance) {
    }
}
