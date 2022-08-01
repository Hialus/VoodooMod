package de.morrien.voodoo.event.wrapper;

import net.minecraft.world.entity.player.Player;

public class PlayerTickEvent {
    public Player player;

    public PlayerTickEvent(Player player) {
        this.player = player;
    }
}
