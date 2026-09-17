package com.minecraft.minigame.game;

import com.minecraft.minigame.BukkitMain;
import com.minecraft.minigame.game.player.GamePlayer;
import com.minecraft.minigame.game.stage.GameStage;
import com.minecraft.minigame.utils.NameTag;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class Game {

    private final List<GamePlayer> players = new ArrayList<>();

    private GameStage stage = GameStage.WAITING;
    private GamePlayer hotPotato;

    private int countdown;
    private int time;

    public Game() {
        new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }

        }.runTaskTimer(BukkitMain.getInstance(), 20L, 20L);
    }

    private void tick() {
        switch (stage) {
            case STARTING:
                tickStarting();
                break;

            case PLAYING:
                tickPlaying();
                break;
        }
    }

    private void tickStarting() {
        if (getAlivePlayers().size() < 2) {
            stage = GameStage.WAITING;
            countdown = 0;

            broadcast("§cJogadores insuficientes. Partida cancelada.");
            return;
        }

        if (countdown <= 0) {
            stage = GameStage.PLAYING;

            startHotPotato();

            broadcast("§a§lPARTIDA INICIADA!");
            return;
        }

        broadcast("§aA partida vai iniciar em §c" + countdown + "§a!");
        countdown--;
    }

    private void tickPlaying() {
        if (getAlivePlayers().size() <= 1) {
            endGame();
            return;
        }

        if (hotPotato == null) {
            startHotPotato();
            return;
        }

        final Player player = hotPotato.getPlayer();

        if (!player.isOnline()) {
            leave(player);
            return;
        }

        player.spigot().sendMessage(new TextComponent("§cExplode em §e" + time + "s"));

        if (time <= 0) {
            explodeHotPotato();
            return;
        }

        time--;
    }

    public void join(final Player player) {
        if (getPlayer(player) != null) {
            return;
        }

        if (stage != GameStage.WAITING) {
            player.sendMessage("§cA partida já começou!");
            return;
        }

        players.add(new GamePlayer(
                player.getUniqueId(),
                player.getName(),
                player
        ));

        preparePlayer(player);
        NameTag.normal(player);

        player.sendMessage("§aVocê entrou na partida!");

        if (players.size() >= 2) {
            startGame();
        }
    }

    public void leave(final Player player) {
        final GamePlayer gamePlayer = getPlayer(player);

        if (gamePlayer == null) {
            NameTag.remove(player);
            return;
        }

        final boolean wasHotPotato = hotPotato == gamePlayer;

        players.remove(gamePlayer);
        NameTag.remove(player);

        if (hotPotato == gamePlayer) {
            hotPotato = null;
        }

        if (stage == GameStage.STARTING) {
            if (getAlivePlayers().size() < 2) {
                stage = GameStage.WAITING;
                countdown = 0;

                broadcast("§cJogadores insuficientes. Partida cancelada.");
            }

            return;
        }

        if (stage != GameStage.PLAYING) {
            return;
        }

        if (getAlivePlayers().size() <= 1) {
            endGame();
            return;
        }

        if (wasHotPotato) {
            startHotPotato();
        }
    }

    private void startGame() {
        if (stage != GameStage.WAITING || players.size() < 2) {
            return;
        }

        stage = GameStage.STARTING;
        countdown = 5;

        final Location spawn = new Location(
                Bukkit.getWorld("world"),
                0.5,
                100,
                -4.5
        );

        for (final GamePlayer gamePlayer : players) {
            final Player player = gamePlayer.getPlayer();

            player.teleport(spawn);

            preparePlayer(player);
            NameTag.normal(player);
        }

        broadcast("§aA partida vai começar!");
    }

    private void startHotPotato() {
        final List<GamePlayer> alivePlayers = getAlivePlayers();

        if (alivePlayers.isEmpty()) {
            return;
        }

        setHotPotato(
                alivePlayers.get(
                        ThreadLocalRandom.current().nextInt(
                                alivePlayers.size()
                        )
                )
        );

        time = 30;
    }

    public void setHotPotato(final Player player) {
        final GamePlayer gamePlayer = getPlayer(player);

        if (gamePlayer == null || !gamePlayer.isAlive()) {
            return;
        }

        setHotPotato(gamePlayer);
    }

    private void setHotPotato(final GamePlayer gamePlayer) {
        if (hotPotato != null) {
            final Player previous = hotPotato.getPlayer();

            previous.getInventory().setHelmet(null);
            NameTag.normal(previous);
        }

        hotPotato = gamePlayer;

        final Player player = gamePlayer.getPlayer();

        player.getInventory().setHelmet(
                new ItemStack(Material.TNT)
        );

        NameTag.hotPotato(player);

        player.sendMessage("§cVocê está com a batata!");
    }

    private void explodeHotPotato() {
        final GamePlayer loser = hotPotato;

        if (loser == null) {
            return;
        }

        final Player player = loser.getPlayer();

        player.getInventory().setHelmet(null);
        player.getWorld().createExplosion(
                player.getLocation(),
                0F
        );

        loser.setSpectator();

        player.setGameMode(GameMode.SPECTATOR);
        NameTag.spectator(player);

        player.sendMessage(
                "§c§lBOOM! §7Você foi eliminado!"
        );

        hotPotato = null;

        if (getAlivePlayers().size() <= 1) {
            endGame();
            return;
        }

        startHotPotato();
    }

    private void endGame() {
        if (stage != GameStage.PLAYING) {
            return;
        }

        stage = GameStage.ENDING;

        if (hotPotato != null) {
            hotPotato.getPlayer()
                    .getInventory()
                    .setHelmet(null);

            hotPotato = null;
        }

        final List<GamePlayer> alivePlayers = getAlivePlayers();

        if (alivePlayers.size() == 1) {
            alivePlayers.get(0).getPlayer().sendMessage(
                    "§a§lVOCÊ GANHOU O HOT POTATO!"
            );
        }

        broadcast("§cPartida finalizada!");

        Bukkit.getScheduler().runTaskLater(
                BukkitMain.getInstance(),
                () -> Bukkit.spigot().restart(),
                20L * 3
        );
    }

    private void preparePlayer(final Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
    }

    public GamePlayer getPlayer(final Player player) {
        for (final GamePlayer gamePlayer : players) {
            if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                return gamePlayer;
            }
        }

        return null;
    }

    public List<GamePlayer> getAlivePlayers() {
        final List<GamePlayer> alivePlayers = new ArrayList<>();

        for (final GamePlayer player : players) {
            if (player.isAlive()) {
                alivePlayers.add(player);
            }
        }

        return alivePlayers;
    }

    public boolean isInvulnerable() {
        return stage.isInvulnerable();
    }

    private void broadcast(final String message) {
        for (final GamePlayer gamePlayer : players) {
            final Player player = gamePlayer.getPlayer();

            if (player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }
}