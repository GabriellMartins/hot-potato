package com.minecraft.minigame.command;

import com.minecraft.minigame.game.Game;
import com.minecraft.minigame.game.stage.GameStage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {

    private final Game game;

    public StartCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String s,
            String[] strings
    ) {

        if (game.getPlayers().size() < 2) {
            sender.sendMessage("§cPrecvisa ter pelo menos 2 jogadores para iniciar"
            );
            return true;

        }

        if (game.getStage() != GameStage.WAITING) {
            sender.sendMessage("§aA partida está acontecendo."
            );
            return true;
        }

        var location = new Location(
                Bukkit.getWorld("world"),
                0,
                100,
                -4
        );
        game.startGame(location);
        sender.sendMessage("§aPartida iniciada!"
        );
        return true;


    }

}
