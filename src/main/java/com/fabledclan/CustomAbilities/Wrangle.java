package com.fabledclan.CustomAbilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Wrangle extends StaminaAbility {
    public Wrangle(String name, int requiredStaminaLevel, int staminaCost) {
        super(name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;

        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
                player.getLocation().getDirection(), 15, entity -> !entity.getUniqueId().equals(player.getUniqueId()));
        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
            player.sendMessage(ChatColor.RED + "No target found for Wrangle!");
            return;
        }

        LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();

        // Calculate the direction from the target to the player
        Vector direction = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();

        // Apply a velocity to the target entity, effectively pulling it towards the player
        target.setVelocity(direction.multiply(1.5)); // Adjust the multiplier as needed to control the strength of the pull

        // Reduce the player's stamina
    }
}
