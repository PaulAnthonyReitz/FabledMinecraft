package com.fabledclan.CustomAbilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.fabledclan.Abilities;

// This is the base abstract class for all spells
// It stores the cooldown, which is an overloaded operator in the constructor
// It also stores the name, cooldowns for players, and alertmessage for players

// This class has an abstract method called 'cast' which is filled out while creating a new ability

public abstract class Ability {
    private static Plugin plugin;
    private static Abilities abilities;
    private final String name;
    private int cooldown = 1000; // default cooldown is 1s (1000ms)
    private Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, Long> alertMessage = new HashMap<>();
    private static int ALERT_COOLDOWN = 5000; // cooldown for alert message (default 5 seconds) (measured in ms)
    private static final String KEY = "embedded_ability";

    public Ability(String n) {
        this.name = n;
    }

    // Overloaded constructor to change the default cooldown
    public Ability(String n, int cd) {
        this.name = n;
        this.cooldown = cd;
    }

    public abstract void cast(Player player); // method to fill out when initializing a new ability

    // Checks if a player is on a cooldown
    public Boolean isOnCooldown(Player player) {
        long time = System.currentTimeMillis();
        long previous = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        long difference = time - previous;
        if (difference <= cooldown) {
            player.sendMessage(
                    ChatColor.RED
                            + String.format("%s ability is on cooldown! Please wait %d seconds", name, (int)(difference / 1000)));
            return true;
        }
        return false;
    }

    // adds a given player to the cooldowns map
    public void addPlayerToCooldowns(Player player) {
        long time = System.currentTimeMillis();
        cooldowns.put(player.getUniqueId(), time);
    }

    public long getCooldown(Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), 0L);
    }

    public String getName() {
        return name;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(Plugin p) {
        plugin = p;
    }

    public static Abilities getAbilities() {
        return abilities;
    }

    public static void setAbilities(Abilities a) {
        abilities = a;
    }

    // checks if the player can be sent another alert
    public Boolean alertMessageReady(UUID playerID) {
        long time = System.currentTimeMillis();
        long previous = alertMessage.getOrDefault(playerID, 0L);
        if (time - previous < ALERT_COOLDOWN) return false;
        alertMessage.put(playerID, time);
        return true;
    }

    // another abstract class that gets filled out in SpellAbility and StaminaAbility classes
    public abstract Boolean failedCastChecks(Player player);

    public static String getKey() {
        return KEY;
    }
}
