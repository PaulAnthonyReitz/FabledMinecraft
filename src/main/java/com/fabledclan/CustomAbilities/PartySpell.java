package com.fabledclan.CustomAbilities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import com.fabledclan.Main;

public class PartySpell extends SpellAbility {

    public PartySpell(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;
    
        // Play party music at the player's location
        player.getWorld().playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 10.0f, 1.0f);
    
        // Spawn RGB sheep
        for (int i = 0; i < 10; i++) {
            Sheep sheep = (Sheep) player.getWorld().spawnEntity(player.getLocation(), EntityType.SHEEP);
            sheep.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
        }
    
        // Change the color of the sheep periodically
        BukkitTask sheepTask = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Sheep) {
                    Sheep sheep = (Sheep) entity;
                    DyeColor newColor = DyeColor.values()[new Random().nextInt(DyeColor.values().length)];
                    sheep.setColor(newColor);
                }
            }
        }, 0, 20); // 20 ticks = 1 second
    
        // Launch fireworks of different colors periodically
    // Launch fireworks around the player continuously for 30 seconds
    BukkitTask fireworkTask = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
        Location fireworkLocation = player.getLocation().add(Math.random() * 4 - 2, Math.random() * 4 + 2,
                Math.random() * 4 - 2); // Added "+ 2" to the y-coordinate
        Firework firework = (Firework) player.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED)
                .withColor(Color.GREEN)
                .withColor(Color.BLUE)
                .with(Type.BURST)
                .trail(true)
                .build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }, 0, 20); // 20 ticks = 1 second

    
        // Schedule the removal of all sheep and fireworks after 1 minute
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Sheep || entity instanceof Firework) {
                    entity.remove();
                }
            }
            sheepTask.cancel();
            fireworkTask.cancel();
        }, 20 * 60); // 20 ticks per second * 60 seconds
    }
}    
