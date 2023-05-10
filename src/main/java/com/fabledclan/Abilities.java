package com.fabledclan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

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
                    int currentStamina = getPlayerStamina(player);
                    if (currentStamina < 100) {
                        playerStamina.put(playerId, Math.min(currentStamina + 2, 100));
                    }

                    // Regenerate mana
                    int currentMana = getPlayerMana(player);
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
        int currentHealth = (int) player.getHealth();
        Integer stamina = getPlayerStamina(player);
        Integer mana = getPlayerMana(player);
        String hpSymbol = ChatColor.RED + "\u2764" + ChatColor.RESET;
        String staminaSymbol = ChatColor.GREEN + "\u26A1" + ChatColor.RESET;
        String manaSymbol = ChatColor.BLUE + "\u2726" + ChatColor.RESET;
        String actionBarText = ChatColor.RED + "" + currentHealth + hpSymbol + " " + ChatColor.GREEN + stamina
                + staminaSymbol + " " + ChatColor.BLUE + mana + manaSymbol;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarText));
    }

    public int getPlayerMana(Player player) {
        return playerMana.getOrDefault(player.getUniqueId(), 0);
    }

    public int getPlayerStamina(Player player) {
        return playerStamina.getOrDefault(player.getUniqueId(), 0);
    }

    public void setPlayerMana(Player player, int mana) {
        playerMana.put(player.getUniqueId(), mana);
    }

    public void setPlayerStamina(Player player, int stamina) {
        playerStamina.put(player.getUniqueId(), stamina);
    }

    public Map<UUID, Integer> getPlayerManaMap() {
        return playerMana;
    }

    public Map<UUID, Integer> getPlayerStaminaMap() {
        return playerStamina;
    }

}
