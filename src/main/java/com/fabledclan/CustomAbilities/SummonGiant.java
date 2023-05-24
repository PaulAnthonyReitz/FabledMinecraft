package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;

public class SummonGiant extends SpellAbility {
    public SummonGiant(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;
    
        // Get the targeted location
        Location targetLocation = player.getTargetBlockExact(100).getLocation();
    
        // Create a smoke circle at the target location
        AreaEffectCloud smoke = (AreaEffectCloud) targetLocation.getWorld().spawnEntity(targetLocation, EntityType.AREA_EFFECT_CLOUD);
        smoke.setRadius(3.0f); // Set the radius of the smoke circle
        smoke.setParticle(Particle.SMOKE_LARGE); // Set the particle type to large smoke
        smoke.setDuration(30 * 20); // Set the duration to 30 seconds
    
        // Schedule the summoning of the giant after 30 seconds
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            // Summon the giant at the target location
            Giant giant = (Giant) targetLocation.getWorld().spawnEntity(targetLocation, EntityType.GIANT);
    
            // Increase the giant's movement speed
            AttributeInstance giantSpeed = giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (giantSpeed != null) {
                giantSpeed.setBaseValue(giantSpeed.getBaseValue() * 2); // Double the giant's movement speed
            }
    
            // Schedule the giant to be removed after 5 minutes
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (!giant.isDead()) {
                    giant.remove();
                }
            }, 5 * 60 * 20); // 5 minutes * 60 seconds/minute * 20 ticks/second
        }, 30 * 20);
    }
}
