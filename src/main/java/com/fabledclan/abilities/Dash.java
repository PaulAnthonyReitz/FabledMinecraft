package com.fabledclan.abilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.fabledclan.Abilities;
import com.fabledclan.StaminaAbility;

public class Dash extends StaminaAbility {
    public Dash(Plugin plugin, Abilities abilities, String name, int requiredStaminaLevel, int staminaCost) {
        super(plugin, abilities, name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        
        // Perform the dash ability
        Vector dashDirection = player.getLocation().getDirection().normalize().multiply(2); // Adjust the multiplier for desired dash strength
        player.setVelocity(player.getVelocity().add(dashDirection));
    }
}