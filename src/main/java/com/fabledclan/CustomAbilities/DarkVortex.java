package com.fabledclan.CustomAbilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class DarkVortex extends SpellAbility {
    public DarkVortex(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;

        Location vortexLocation = player.getTargetBlock(null, 30).getLocation().add(0, 1, 0);
        BoundingBox vortexArea = BoundingBox.of(vortexLocation, 10, 10, 10);

        new BukkitRunnable() {
            int duration = 20 * 15; // 15 seconds

            @Override
            public void run() {
                if (duration <= 0) {
                    cancel();
                }

                // Spawn particle effects to create a swirly visual
                for (double i = 0; i < 2 * Math.PI; i += Math.PI / 16) {
                    double x = Math.cos(i) * 6;
                    double z = Math.sin(i) * 6;
                    vortexLocation.getWorld().spawnParticle(Particle.SPELL_MOB,
                            vortexLocation.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                }

                for (Entity entity : vortexLocation.getWorld().getNearbyEntities(vortexLocation, 10, 10, 10)) {
                    if (vortexArea.contains(entity.getLocation().toVector())) {
                        Vector direction = vortexLocation.toVector().subtract(entity.getLocation().toVector())
                                .normalize();
                        entity.setVelocity(entity.getVelocity().add(direction.multiply(0.5)));
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).damage(1, player);
                        }
                    }
                }

                duration--;
            }
        }.runTaskTimer(getPlugin(), 0, 1);
    }
}
