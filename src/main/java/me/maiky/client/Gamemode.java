package me.maiky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@AllArgsConstructor
public enum Gamemode {

    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    @Getter private final int id;

}
