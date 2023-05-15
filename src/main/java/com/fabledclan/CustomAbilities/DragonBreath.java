package com.fabledclan.CustomAbilities;

import org.bukkit.util.Vector;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.Location;

public class DragonBreath extends SpellAbility {

    public DragonBreath() {
        super("dragon_breath", 1, 50);
    }

    public void cast(Player player) {
        // Reduce the player's magic energy by the required amount
        if (failedCastChecks(player)) return;
        // Get the direction the player is facing
        Vector direction = player.getLocation().getDirection();

        // Calculate the starting point of the dragon breath
        Location startPoint = player.getEyeLocation().add(direction.multiply(2));

        // Spawn a dragon fireball at the starting point
        DragonFireball fireball = (DragonFireball) player.getWorld().spawnEntity(startPoint,
                EntityType.DRAGON_FIREBALL);

        // Set the shooter to the player to prevent self-damage
        fireball.setShooter(player);

        // Set the direction and velocity of the dragon fireball
        fireball.setDirection(direction);
        fireball.setVelocity(direction.multiply(2));

        // Set the explosion power of the dragon fireball
        fireball.setYield(0);

        // Set the fire duration of the dragon fireball
        fireball.setFireTicks(40);

        // Register an event listener to handle the dragon fireball collision
        getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onProjectileHit(ProjectileHitEvent event) {
                if (event.getEntity().equals(fireball)) {
                    // Check if the dragon fireball hit an entity
                    if (event.getHitEntity() != null) {
                        // Set the hit entity on fire
                        event.getHitEntity().setFireTicks(40);
                    }

                    // Remove the dragon fireball
                    fireball.remove();
                }
            }
        }, getPlugin());
    }
}
