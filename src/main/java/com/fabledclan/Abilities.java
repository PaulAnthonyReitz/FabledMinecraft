package com.fabledclan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Abilities implements Listener {

    final Map<UUID, Integer> playerStamina = new HashMap<>();
    final Map<UUID, Integer> playerMana = new HashMap<>();
    private Main plugin;

    public Abilities(Main plugin) {
        this.plugin = plugin;
    }
    
    public void startTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();

                    // Regenerate stamina
                    int currentStamina = playerStamina.getOrDefault(playerId, 0);
                    if (currentStamina < 100) {
                        playerStamina.put(playerId, Math.min(currentStamina + 2, 100));
                    }

                    // Regenerate mana
                    int currentMana = playerMana.getOrDefault(playerId, 0);
                    if (currentMana < 100) {
                        playerMana.put(playerId, Math.min(currentMana + 2, 100));
                    }

                    // Update action bar text
                    sendActionBarText(player);
                }
            }
        }.runTaskTimer(plugin, 0, 40); // 20 ticks = 1 second
    }

    
    private void sendActionBarText(Player player) {
        UUID playerId = player.getUniqueId();
        int currentHealth = (int) player.getHealth();
        int maxHealth = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        Integer stamina = playerStamina.get(playerId);
        Integer mana = playerMana.get(playerId);
        String hpSymbol = ChatColor.RED + "\u2764" + ChatColor.RESET;
        String staminaSymbol = ChatColor.GREEN + "\u26A1" + ChatColor.RESET;
        String manaSymbol = ChatColor.BLUE + "\u2726" + ChatColor.RESET;
        String actionBarText = ChatColor.RED + "" + currentHealth + hpSymbol + " " + ChatColor.GREEN + stamina + staminaSymbol + " " + ChatColor.BLUE + mana + manaSymbol;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarText));
    }

    public Map<UUID, Integer> getPlayerMana() {
        return playerMana;
    }
    public Map<UUID, Integer> getPlayerStamina() {
        return playerStamina;
    }
    

}
