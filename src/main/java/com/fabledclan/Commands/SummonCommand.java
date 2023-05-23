package com.fabledclan.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;
import com.fabledclan.Listeners.Abilities;

public class SummonCommand extends CommandClass implements Listener {

    private final Map<UUID, UUID> pendingSummonRequests = new HashMap<>();

    public SummonCommand() {
        super("summon");
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
        int currentMana = Abilities.getPlayerMana(player);

        if (currentMana < requiredMana) {
            player.sendMessage(ChatColor.BLUE + "Not enough magic to use Summon spell!");
            return true;
        }

        // Deduct mana
        Abilities.setPlayerMana(player, currentMana - requiredMana);

        pendingSummonRequests.put(targetId, playerId);
        targetPlayer.sendMessage(ChatColor.GREEN + player.getName()
                + " would like to teleport you to them. Type 'yes' to accept, 'no' to decline.");
        player.sendMessage(ChatColor.GREEN + "Summon request sent to " + targetPlayer.getName() + ".");
        return true;
    }
    
    public Map<UUID, UUID> getPendingSummonRequests() {
        return pendingSummonRequests;
    }
}
