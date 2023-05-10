package com.fabledclan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SummonCommand implements CommandExecutor, Listener {

    private final Map<UUID, UUID> pendingSummonRequests = new HashMap<>();
    private final Abilities abilities;
    private Main plugin;

    public SummonCommand(Main plugin, Abilities abilities) {
        this.plugin = plugin;
        this.abilities = abilities;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /summon <player>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        UUID targetId = targetPlayer.getUniqueId();

        int requiredMana = 100;
        int currentMana = abilities.getPlayerMana(player);

        if (currentMana < requiredMana) {
            player.sendMessage(ChatColor.BLUE + "Not enough magic to use Summon spell!");
            return true;
        }

        // Deduct mana
        abilities.setPlayerMana(player, currentMana - requiredMana);

        pendingSummonRequests.put(targetId, playerId);

        targetPlayer.sendMessage(ChatColor.GREEN + player.getName()
                + " would like to teleport you to them. Type 'yes' to accept, 'no' to decline.");
        player.sendMessage(ChatColor.GREEN + "Summon request sent to " + targetPlayer.getName() + ".");
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String message = event.getMessage().toLowerCase();
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
                        }.runTask(plugin);
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
                        }.runTask(plugin);
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

    public Map<UUID, UUID> getPendingSummonRequests() {
        return pendingSummonRequests;
    }
}
