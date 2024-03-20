package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class MagicMissile extends SpellAbility {

    private static final double DAMAGE = 10.0;
    private static final int SLOW_DURATION = 200; // Ticks (10 seconds)
    private static final int SLOW_AMPLIFIER = 1;
    private static final int MISSILE_RANGE = 100;
    private static final int MISSILE_SPEED = 1;
    private static final int PARTICLE_COUNT = 20;
    private static final double PARTICLE_OFFSET = 0.1;

    public MagicMissile() {
        super("magic_missile", 1, 25);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) {
            return;
        }

        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                MISSILE_RANGE
        );

        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
            player.sendMessage(ChatColor.RED + "No target found for Magic Missile!");
            return;
        }

        LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
        Vector direction = target.getEyeLocation().subtract(player.getEyeLocation()).toVector().normalize();

        spawnMissileParticles(player, target, direction);
        damageTarget(target, player);
        applySlowEffect(target);
    }

    private void spawnMissileParticles(Player player, LivingEntity target, Vector direction) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Location spawnLocation = player.getEyeLocation().add(direction.clone().multiply(i * PARTICLE_OFFSET));
            player.getWorld().spawnParticle(Particle.FLAME, spawnLocation, 0);
        }

        spawnFirework(player, target, direction);
    }

    private void spawnFirework(Player player, LivingEntity target, Vector direction) {
        Location spawnLocation = player.getEyeLocation().add(direction.multiply(MISSILE_RANGE));
        Firework firework = (Firework) player.getWorld().spawnEntity(spawnLocation, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BURST)
                .trail(true)
                .build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
        firework.setVelocity(direction.clone().multiply(MISSILE_SPEED));
        Bukkit.getScheduler().runTaskLater(getPlugin(), firework::detonate, 20 * 1);
    }

    private void damageTarget(LivingEntity target, Player player) {
        target.damage(DAMAGE, player);
    }

    private void applySlowEffect(LivingEntity target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SLOW_DURATION, SLOW_AMPLIFIER));
    }
}