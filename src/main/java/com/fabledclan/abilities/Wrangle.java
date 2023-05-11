package com.fabledclan.abilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.fabledclan.StaminaAbility;

public class Wrangle extends StaminaAbility {
    public Wrangle(String name, int requiredStaminaLevel, int staminaCost) {
        super(name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
                player.getLocation().getDirection(), 100);
        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)
                || rayTraceResult.getHitEntity().equals(player)) {
            player.sendMessage(ChatColor.RED + "Wrangle only works on entities other than yourself!");
            return;
        }
        // LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();

        // Make the player mount the target entity
        // ...
    }
}
