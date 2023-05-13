package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlagueSwarm extends SpellAbility {
    public PlagueSwarm(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        for (int i = 0; i < 10; i++) {
            Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0, 1,
                    0), EntityType.BAT);
            bat.setMetadata("PlagueSwarm", new FixedMetadataValue(getPlugin(), true));

            new BukkitRunnable() {
                int duration = 20 * 5; // 5 seconds

                @Override
                public void run() {
                    if (duration <= 0 || bat.isDead()) {
                        bat.remove();
                        cancel();
                    }

                    Entity target = null;
                    double minDistance = Double.MAX_VALUE;

                    for (Entity entity : bat.getNearbyEntities(10, 10, 10)) {
                        if (entity instanceof LivingEntity && entity != player && !(entity instanceof Bat)) {
                            double distance = entity.getLocation().distanceSquared(bat.getLocation());
                            if (distance < minDistance) {
                                minDistance = distance;
                                target = entity;
                            }
                        }
                    }

                    if (target != null) {
                        Vector direction = target.getLocation().toVector().subtract(bat.getLocation().toVector())
                                .normalize();
                        bat.setVelocity(direction.multiply(0.5));

                        if (target.getLocation().distance(bat.getLocation()) < 2) {
                            if (target instanceof LivingEntity) {
                                LivingEntity livingEntity = (LivingEntity) target;
                                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 *
                                        5, 1));
                                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5,
                                        1));
                            }
                        }
                    }

                    duration--;
                }
            }.runTaskTimer(getPlugin(), 0, 1);
        }
    }
}
