package com.fabledclan.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;
import com.fabledclan.Commands.SummonCommand;

import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private Map<UUID, UUID> getPendingSummonRequests() {
        return SummonCommand.getPendingSummonRequests();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String message = event.getMessage().toLowerCase();

        if (message.equalsIgnoreCase("For Fabled!")) {
            // Create the potion effect
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1); // Speed II for 20 seconds

            // Use the server scheduler to add the potion effect in the main server thread
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                player.addPotionEffect(speedBuff);
            });

            event.setCancelled(true);
        } else {
            Map<UUID, UUID> pendingSummonRequests = getPendingSummonRequests();

            if (pendingSummonRequests.containsKey(playerId)) {
                event.setCancelled(true);
                UUID requesterId = pendingSummonRequests.remove(playerId);

                if (message.equals("yes")) {
                    Player requester = Bukkit.getPlayer(requesterId);
                    if (requester != null) {
                        if (player == requester) {
                            player.sendMessage(ChatColor.RED + "WHAT IN GODS NAME HAVE YOU DONE");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.teleport(player.getWorld().getSpawnLocation().add(Math.random() * 1000 - 500, 0,
                                            Math.random() * 1000 - 500));
                                }
                            }.runTask(Main.getPlugin());
                            return;
                        } else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.teleport(requester);
                                    player.sendMessage(
                                            ChatColor.GREEN + "You have been teleported to " + requester.getName() + "!");
                                    requester.sendMessage(
                                            ChatColor.GREEN + player.getName() + " has been teleported to you!");
                                }
                            }.runTask(Main.getPlugin());
                        }
                    }
                } else if (message.equals("no")) {
                    player.sendMessage(ChatColor.RED + "Teleport request declined.");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Please type 'yes' or 'no' to respond to the teleport request.");
                    pendingSummonRequests.put(playerId, requesterId);
                }
            }
        }
    }
}
