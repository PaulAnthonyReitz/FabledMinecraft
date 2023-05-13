package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Dash extends StaminaAbility {
    public Dash(String name, int requiredStaminaLevel, int staminaCost) {
        super(name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        
        // Perform the dash ability
        Vector dashDirection = player.getLocation().getDirection().normalize().multiply(2); // Adjust the multiplier for desired dash strength
        player.setVelocity(player.getVelocity().add(dashDirection));
    }
}