package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

        // Summon the giant at the target location
        Giant giant = (Giant) targetLocation.getWorld().spawnEntity(targetLocation,
                EntityType.GIANT);

        // Set the giant's size to a larger scale

        // Increase the giant's movement speed
        AttributeInstance giantSpeed = giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (giantSpeed != null) {
            giantSpeed.setBaseValue(giantSpeed.getBaseValue());
        }

        // Set the giant's target to the player
        giant.setTarget(player);

        // Schedule the giant to be removed after 30 seconds
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (!giant.isDead()) {
                giant.remove();
            }
        }, 30 * 20);
    }

}
