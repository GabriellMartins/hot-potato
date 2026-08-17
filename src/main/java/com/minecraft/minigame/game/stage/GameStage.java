package com.minecraft.minigame.game.stage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameStage {

    WAITING(false),
    STARTING(true),
    PLAYING(false),
    ENDING(true);

    private final boolean invulnerable;


}
