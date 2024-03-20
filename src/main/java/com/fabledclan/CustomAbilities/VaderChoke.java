package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.fabledclan.Main;

public class VaderChoke extends SpellAbility {

    public VaderChoke() {
        super("vader_choke", 1, 50);
    }

    public void cast(Player player) {
        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 30, entity -> !entity.getUniqueId().equals(player.getUniqueId()));
        
        LivingEntity target = null;
        if (rayTraceResult != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
            target = (LivingEntity) rayTraceResult.getHitEntity();
        } else {
            player.sendMessage(ChatColor.RED + "No valid target found for Vader Choke!");
            return;
        }

        // Check if the player has enough resources and cooldown before applying the effect
        if (failedCastChecks(player))
            return;

        // Apply the choke effect
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10 * 20, 1)); // Apply Levitation effect for 10 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 1)); // Apply Slowness effect for 10 seconds

        // Schedule a repeating task to deal damage to the target every second
        final LivingEntity finalTargetLoop = target;
        int damageTaskId = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
            if (finalTargetLoop.isValid()) {
                double damagePerTick = 2.0; // Adjust the damage amount as needed
                finalTargetLoop.damage(damagePerTick, player);
            }
        }, 0L, 20L).getTaskId(); // 20 ticks = 1 second

        // Play a sound effect when the ability is cast
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);

        // Play a repeating choking sound effect while the choke effect is active
        int soundTaskId = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
            if (finalTargetLoop.isValid()) {
                finalTargetLoop.getWorld().playSound(finalTargetLoop.getLocation(), Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 1.0f, 1.0f);
            }
        }, 0L, 20L).getTaskId();

        // Schedule a repeating task to spawn particles around the target
        int particleTaskId = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
            if (finalTargetLoop.isValid()) {
                finalTargetLoop.getWorld().spawnParticle(Particle.SMOKE_NORMAL, finalTargetLoop.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.05);
            }
        }, 0L, 5L).getTaskId(); // Spawn particles every 5 ticks (0.25 seconds)

        // Schedule the removal of the Levitation, Slowness effects, and cancellation of tasks after 10 seconds
        final LivingEntity finalTarget = target;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            if (finalTarget.isValid()) {
                finalTarget.removePotionEffect(PotionEffectType.LEVITATION); // Remove the Levitation effect
                finalTarget.removePotionEffect(PotionEffectType.SLOW); // Remove the Slowness effect
                Bukkit.getScheduler().cancelTask(damageTaskId);
                Bukkit.getScheduler().cancelTask(soundTaskId);
                Bukkit.getScheduler().cancelTask(particleTaskId);
            }
        }, 10 * 20);

        // Add visual effects (force field or energy beam)
        // Replace this comment with your code to create the visual effect

        // Add sound quotes
        // Replace this comment with your code to play sound quotes from Darth Vader
    }
}