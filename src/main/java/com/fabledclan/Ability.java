package com.fabledclan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

// This is the base abstract class for all spells
// It stores the cooldown, which is an overloaded operator in the constructor
// It also stores the name, cooldowns for players, and alertmessage for players

// This class has an abstract method called 'cast' which is filled out while creating a new ability

public abstract class Ability {
    private final Plugin plugin;
    private final Abilities abilities;
    private final String name;
    private int cooldown = 1000; // default cooldown is 1s (1000ms)
    private Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, Long> alertMessage = new HashMap<>();
    private final int ALERT_COOLDOWN = 5000; // cooldown for alert message (default 5 seconds) (measured in ms)

    public Ability(Plugin p, Abilities a, String n) {
        this.plugin = p;
        this.abilities = a;
        this.name = n;
    }

    // Overloaded constructor to change the default cooldown
    public Ability(Plugin p, Abilities a, String n, int cd) {
        this.plugin = p;
        this.abilities = a;
        this.name = n;
        this.cooldown = cd;
    }

    public abstract void cast(Player player); // method to fill out when initializing a new ability

    public Abilities getAbilities() {
        return abilities;
    }

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

    public Plugin getPlugin() {
        return plugin;
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
}
