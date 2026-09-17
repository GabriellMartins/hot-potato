package com.minecraft.minigame.game.player;

import com.minecraft.minigame.game.player.state.PlayerState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class GamePlayer {

    private final UUID uniqueId;
    private final String name;
    private final Player player;

    private PlayerState state = PlayerState.ALIVE;

    public boolean isAlive() {
        return state == PlayerState.ALIVE;
    }

    public boolean isSpectator() {
        return state == PlayerState.SPECTATOR;
    }

    public void setSpectator() {
        state = PlayerState.SPECTATOR;
    }

}