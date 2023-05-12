package com.fabledclan.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Abilities implements Listener {

    private static final Map<UUID, Integer> playerStamina = new HashMap<>();
    private static final Map<UUID, Integer> playerMana = new HashMap<>();

    public Abilities() {
        startTasks();
    }

    public void startTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    // Regenerate stamina
                    int currentStamina = getPlayerStamina(player);
                    if (currentStamina < 100) {
                        setPlayerStamina(player, Math.min(currentStamina + 2, 100));
                    }

                    // Regenerate mana
                    int currentMana = getPlayerMana(player);
                    if (currentMana < 100) {
                        setPlayerMana(player, Math.min(currentMana + 2, 100));
                    }

                    if (player.getGameMode() == GameMode.CREATIVE) {
                        setPlayerMana(player, 100);
                        setPlayerStamina(player, 100);
                    }

                    // Update action bar text
                    sendActionBarText(player);
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 40); // 20 ticks = 1 second
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

    public static int getPlayerMana(Player player) {
        return playerMana.getOrDefault(player.getUniqueId(), 0);
    }

    public static int getPlayerStamina(Player player) {
        return playerStamina.getOrDefault(player.getUniqueId(), 0);
    }

    public static void setPlayerMana(Player player, int mana) {
        playerMana.put(player.getUniqueId(), mana);
    }

    public static void setPlayerStamina(Player player, int stamina) {
        playerStamina.put(player.getUniqueId(), stamina);
    }

    public Map<UUID, Integer> getPlayerManaMap() {
        return playerMana;
    }

    public Map<UUID, Integer> getPlayerStaminaMap() {
        return playerStamina;
    }

}
