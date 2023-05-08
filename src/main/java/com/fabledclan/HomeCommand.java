package com.fabledclan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private Map<UUID, Long> cooldowns = new HashMap<>();

    public HomeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (cooldowns.containsKey(playerId)) {
            long cooldownEnd = cooldowns.get(playerId);
            long remainingTime = cooldownEnd - System.currentTimeMillis();
            if (remainingTime > 0) {
                player.sendMessage("You must wait " + (remainingTime / 1000) + " seconds before using this command again.");
                return true;
            }
        }

        // Warp the player to their home point after a 20-second delay
        new BukkitRunnable() {
            @Override
            public void run() {
                // Replace this with your actual home point teleportation logic
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }.runTaskLater(plugin, 20 * 20); // 20 seconds delay (20 ticks per second)

        // Set the cooldown for the player
        long cooldownTime = System.currentTimeMillis() + (20 * 60 * 1000); // 20 minutes cooldown
        cooldowns.put(playerId, cooldownTime);

        player.sendMessage("Teleporting to home in 20 seconds.");
        return true;
    }
}
