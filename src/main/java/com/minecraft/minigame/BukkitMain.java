package com.minecraft.minigame;

import com.minecraft.minigame.command.StartCommand;
import com.minecraft.minigame.game.Game;
import com.minecraft.minigame.game.listener.game.GameListener;
import com.minecraft.minigame.utils.NameTag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitMain extends JavaPlugin {

    @Getter
    private static BukkitMain instance;

    @Getter
    private Game game;

    @Override
    public void onEnable() {
        instance = this;

        NameTag.setup();

        game = new Game();

        getServer().getPluginManager().registerEvents(new GameListener(game), this);

        getCommand("start").setExecutor(new StartCommand(game));

    }

}