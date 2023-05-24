package com.fabledclan.CustomAbilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import com.fabledclan.Main;

import org.bukkit.entity.Entity;

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// CURRENTLY VADER CHOKE WILL TAKE AWAY MANA FROM THE PLAYER BEFORE CHECKING A VALID TARGET
// PROBABLY SHOULD BE FIXED BUT I'M LAZY RIGHT NOW SO GIVE IT A BIT
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class VaderChoke extends SpellAbility {
    public VaderChoke(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;
    
        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 15, entity -> !entity.getUniqueId().equals(player.getUniqueId()));
        LivingEntity target = null;
    
        if (rayTraceResult != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
            target = (LivingEntity) rayTraceResult.getHitEntity();
        } else {
            player.sendMessage(ChatColor.RED + "No valid target found for Vader Choke!");
            return;
        }
    
        // Apply the choke effect
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 1)); // Apply Levitation effect for 5 seconds
    
        // Prevent the target from moving for 5 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 10)); // Apply Slowness effect for 5 seconds

        // Schedule the removal of the Levitation and Slowness effects after 5 seconds
        final LivingEntity finalTarget = target;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            if (finalTarget.isValid()) {
                finalTarget.removePotionEffect(PotionEffectType.LEVITATION); // Remove the Levitation effect
                finalTarget.removePotionEffect(PotionEffectType.SLOW); // Remove the Slowness effect
            }
        }, 5 * 20);
    }    
}
