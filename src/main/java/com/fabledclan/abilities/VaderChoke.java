package com.fabledclan.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.entity.Entity;

import com.fabledclan.Abilities;
import com.fabledclan.SpellAbility;

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// CURRENTLY VADER CHOKE WILL TAKE AWAY MANA FROM THE PLAYER BEFORE CHECKING A VALID TARGET
// PROBABLY SHOULD BE FIXED BUT I'M LAZY RIGHT NOW SO GIVE IT A BIT
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

public class VaderChoke extends SpellAbility {
    public VaderChoke(Plugin plugin, Abilities abilities, String name, int requiredMagicLevel, int manaCost) {
        super(plugin, abilities, name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;
        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
                player.getLocation().getDirection(), 15, entity -> !entity.getUniqueId().equals(player.getUniqueId()));
        LivingEntity target = null;

        if (rayTraceResult != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
            target = (LivingEntity) rayTraceResult.getHitEntity();
        } else {
            Location hitLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(15));
            List<Entity> nearbyEntities = new ArrayList<>(hitLocation.getWorld().getNearbyEntities(hitLocation, 1, 1, 1,
                    entity -> entity instanceof LivingEntity &&
                            !entity.getUniqueId().equals(player.getUniqueId())));

            if (!nearbyEntities.isEmpty()) {
                target = (LivingEntity) nearbyEntities.get(0);
            }
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "No valid target found for Vader Choke!");
            return;
        }

        // Reduce the player's magic energy by the required amount

        // Apply the choke effect
        Location targetLocation = target.getLocation();
        targetLocation.setY(targetLocation.getY() + 2); // Lift the target off the ground by 2 blocks
        target.teleport(targetLocation);

        // Prevent the target from moving for 5 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));

        // Schedule the release of the target after 5 seconds
        final LivingEntity finalTarget = target;
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (finalTarget.isValid()) {
                finalTarget.teleport(targetLocation.subtract(0, 2, 0)); // Lower the target back to the ground
                finalTarget.removePotionEffect(PotionEffectType.SLOW); // Remove the immobilization effect
            }
        }, 5 * 20);
    }
}
