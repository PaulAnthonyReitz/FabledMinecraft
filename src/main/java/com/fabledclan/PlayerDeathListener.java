package com.fabledclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final Main plugin;

    public PlayerDeathListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !victim.getUniqueId().equals(killer.getUniqueId())) {
            DatabaseManager databaseManager = plugin.getDatabaseManager();
            int victimBounty = databaseManager.getBounty(victim.getUniqueId());

            if (victimBounty > 0) {
                // Victim had a bounty, reward the killer and reset the victim's bounty
                //databaseManager.addBounty(killer.getUniqueId(), victimBounty);
                databaseManager.addExp(killer.getUniqueId(), victimBounty);
                databaseManager.setBounty(victim.getUniqueId(), 0);

                Bukkit.broadcastMessage(ChatColor.GREEN + killer.getName() + " has claimed the bounty of " + ChatColor.GOLD + victimBounty + ChatColor.GREEN + " on " + victim.getName() + "!");
            } else {
                // Increase the killer's bounty
                int newBounty = databaseManager.addBounty(killer.getUniqueId(), 100); // You can change the bounty increment value here
                Bukkit.broadcastMessage(ChatColor.RED + killer.getName() + " has murdered " + victim.getName() + "! A bounty of " + ChatColor.GOLD + newBounty + ChatColor.RED + " is now on their head!");
            }
        }
    }
}
