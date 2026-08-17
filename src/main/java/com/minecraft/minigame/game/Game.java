package com.minecraft.minigame.game;

import com.minecraft.minigame.BukkitMain;
import com.minecraft.minigame.game.stage.GameStage;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class Game {

    private GameStage stage = GameStage.WAITING;

    private final List<Player> players = new ArrayList<>();

    private Player hotPotato;

    private int time;

    private int countdown;

    public void startGame(Location location) {

        if (players.size() < 2) {
            return;
        }

        stage = GameStage.STARTING;
        countdown = 5;

        for (Player player : players) {
            player.teleport(location);
            player.getInventory().setHelmet(null);

            player.sendMessage("§aA partida foi iniciada!");
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (stage != GameStage.STARTING) {
                    cancel();
                    return;
                }

                if (players.size() < 2) {
                    return;
                }

                if (countdown <= 0) {
                    cancel();

                    stage = GameStage.PLAYING;
                    RandomHotPotato();
                    return;
                }
                for (Player player : players) {
                    player.sendMessage("§aA partida vai iniciar em §c" + countdown);
                }
                countdown--;
            }

        }.runTaskTimer(BukkitMain.getInstance(), 0L, 20L);

        var location1 = new Location(
                Bukkit.getWorld("world"),
                0.5,
                100,
                0.5
        );


    }

    public void endGame() {
        stage = GameStage.ENDING;
        if (hotPotato != null) {
            hotPotato.getInventory().setHelmet(null);
            hotPotato = null;
        }

        var winner = players.size() == 1 ? players.get(0) : null;

        if (winner != null) {
            Bukkit.broadcastMessage(
                    "§aVocê ganhou o hotpotato!"
            );
        }

        for (Player player : players) {
            player.getInventory().setHelmet(null);
            player.sendMessage("§cPartida finalizada!");
        }
        players.clear();

        new
                BukkitRunnable() {

                    @Override
                    public void run() {
                        stage = GameStage.WAITING;

                    }
                }.runTaskLater(BukkitMain.getInstance(), 20L * 3);
    }

    public void setHotPotato(Player player) {
        if (hotPotato != null) {
            hotPotato.getInventory().setHelmet(null);
        }

        hotPotato = player;


        player.getInventory().setHelmet(new ItemStack(Material.TNT));

        player.sendMessage("§cVocê está com a batata!");
    }

    public void join(Player player) {

        if (players.contains(player)) {
            return;
        }

        if (stage != GameStage.WAITING) {
            player.sendMessage("§aApartida começou!");
            return;
        }
        players.add(player);

        if (players.size() >= 2 && stage == GameStage.WAITING);

        startGame(new Location(
                Bukkit.getWorld("world"),
                0, 100, -4
        ));
    }

    public void leave(Player player) {
        player.remove();

        if (hotPotato == player) {
            player.getInventory().setHelmet(null);
            hotPotato = null;
        }

        player.sendMessage("§cVocê saiu da partida");

        if (players.size() < 2 && stage == GameStage.STARTING) {
            stage = GameStage.WAITING;
        }

        if (players.size() <= 1 && stage == GameStage.PLAYING) {
            endGame();
        }
    }

    private void startHotPotato() {

        time = 30;

        new BukkitRunnable() {

            @Override
            public void run() {

                if (stage != GameStage.PLAYING || hotPotato == null) {
                    cancel();
                    return;
                }

                hotPotato.spigot().sendMessage(
                        new TextComponent(
                                "§CExplode em §e" +
                                        time + "s"
                        )
                );

                if (time <= 0) {

                    cancel();

                    Player loser = hotPotato;

                    loser.getInventory().setHelmet(null);

                    loser.getWorld().createExplosion(
                            loser.getLocation(),
                            0F
                    );

                    players.remove(loser);

                    hotPotato = null;

                    loser.setGameMode(GameMode.SPECTATOR);

                    loser.sendMessage(
                            "§c§lBOOM! §7Você foi eliminado!"
                    );

                    if (players.size() <= 1) {
                        endGame();
                    } else {
                        RandomHotPotato();
                    }

                    return;
                }

                time--;
            }

        }.runTaskTimer(
                BukkitMain.getInstance(),
                0L,
                20L
        );
    }

    private void RandomHotPotato() {
        Player player = players.get(
                new Random().nextInt(players.size())
        );
        setHotPotato(player);

        startHotPotato();

    }

    public boolean isInvulnerable() {
        return stage.isInvulnerable();
    }
}
