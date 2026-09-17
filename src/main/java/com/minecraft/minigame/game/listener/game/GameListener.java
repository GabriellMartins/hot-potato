package com.minecraft.minigame.game.listener.game;

import com.minecraft.minigame.game.Game;
import com.minecraft.minigame.game.player.GamePlayer;
import com.minecraft.minigame.game.stage.GameStage;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class GameListener implements Listener {

    private final Game game;

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player attacker = (Player) event.getDamager();
        final Player target = (Player) event.getEntity();

        if (game.getStage() != GameStage.PLAYING) {
            event.setCancelled(true);
            return;
        }

        final GamePlayer attackerData = game.getPlayer(attacker);
        final GamePlayer targetData = game.getPlayer(target);

        if (attackerData == null || targetData == null) {
            event.setCancelled(true);
            return;
        }


        if (!attackerData.isAlive() || !targetData.isAlive()) {
            event.setCancelled(true);
            return;
        }

        if (game.getHotPotato() != attackerData) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        game.setHotPotato(target);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        game.join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        game.leave(event.getPlayer());
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        if (game.getPlayer(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(final PlayerPickupItemEvent event) {
        if (game.getPlayer(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (game.getPlayer((Player) event.getWhoClicked()) != null) {
            event.setCancelled(true);
        }
    }
}