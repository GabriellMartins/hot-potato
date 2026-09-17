package com.minecraft.minigame.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class NameTag {

    private static final Scoreboard SCOREBOARD =
            Bukkit.getScoreboardManager().getMainScoreboard();

    private NameTag() {
    }

    public static void setup() {
        createTeam("normal", "§a");
        createTeam("spectator", "§7[SPECTATOR] §7");
        createTeam("hotpotato", "§c");
    }

    public static void normal(final Player player) {
        remove(player);

        SCOREBOARD.getTeam("normal").addPlayer(player);
    }

    public static void spectator(final Player player) {
        remove(player);

        SCOREBOARD.getTeam("spectator").addPlayer(player);
    }

    public static void hotPotato(final Player player) {
        remove(player);

        SCOREBOARD.getTeam("hotpotato").addPlayer(player);
    }

    public static void remove(final Player player) {
        SCOREBOARD.getTeam("normal").removePlayer(player);
        SCOREBOARD.getTeam("spectator").removePlayer(player);
        SCOREBOARD.getTeam("hotpotato").removePlayer(player);
    }

    private static void createTeam(final String name, final String prefix) {
        Team team = SCOREBOARD.getTeam(name);

        if (team == null) {
            team = SCOREBOARD.registerNewTeam(name);
        }

        team.setPrefix(prefix);
    }
}