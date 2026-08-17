package com.minecraft.minigame.game.listener.game;

import com.minecraft.minigame.game.Game;
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
public class GameListener implements Listener {

    private final Game game;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        var attacker = (Player) event.getDamager();
        var target = (Player) event.getEntity();

        if (game.getStage() != GameStage.PLAYING) {
            event.setCancelled(true);
            return;
        }

        if (game.getHotPotato() != attacker) {
            event.setCancelled(true);
            return;
        }

        game.setHotPotato(target);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        game.join(player);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        game.leave(player);
    }


    @EventHandler
    public void a(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void b(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void c(InventoryClickEvent event) {
        event.setCancelled(true);
    }

}
