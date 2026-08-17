package com.minecraft.minigame;

import com.minecraft.minigame.command.StartCommand;
import com.minecraft.minigame.game.Game;
import com.minecraft.minigame.game.listener.game.GameListener;
import com.minecraft.minigame.game.stage.GameStage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitMain  extends JavaPlugin {

    @Getter
    @Setter
    private static BukkitMain instance;

    @Getter
    @Setter
    private Game game;

    @Override
    public void onEnable() {
        instance = this;
        game = new Game();

        getServer().getPluginManager().registerEvents(
                new GameListener(game), this
        );
        getCommand("start").setExecutor(
                new StartCommand(game)
        );


    }

}
