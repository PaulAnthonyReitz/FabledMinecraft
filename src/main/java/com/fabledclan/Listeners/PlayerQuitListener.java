package com.fabledclan.Listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.fabledclan.DatabaseManager;
import com.fabledclan.Main;
import com.fabledclan.Player.PlayerStats;
import com.fabledclan.Player.PlayerStatsCache;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerStatsCache playerStatsCache = Main.getPlayerStatsCache();
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Get the player's stats from the cache
        PlayerStats stats = playerStatsCache.get(playerUUID);
        if (stats != null) {
            // Update the player's stats in the database
            DatabaseManager.setPlayerStats(playerUUID, stats.getMovementSpeed(), stats.getAttack(), stats.getDefense(), 
                stats.getMaxHealth(), stats.getExp(), stats.getLevel(), stats.getName(), stats.getMagic(), stats.getStamina());
        }

        // Remove the player's stats from the cache
        playerStatsCache.remove(playerUUID);
    }
}