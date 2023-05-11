package com.fabledclan.abilities;

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

import com.fabledclan.SpellAbility;

public class PartySpell extends SpellAbility {

    public PartySpell(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;

        // Play party music at the player's location
        player.getWorld().playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 10.0f, 1.0f);

        // Spawn RGB sheep and set their color
        BukkitTask sheepTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Sheep) {
                    Sheep sheep = (Sheep) entity;
                    DyeColor currentColor = sheep.getColor();
                    DyeColor newColor;
                    switch (currentColor) {
                        case RED:
                            newColor = DyeColor.GREEN;
                            break;
                        case GREEN:
                            newColor = DyeColor.BLUE;
                            break;
                        case BLUE:
                            newColor = DyeColor.RED;
                            break;
                        default:
                            newColor = DyeColor.RED;
                            break;
                    }
                    sheep.setColor(newColor);
                }
            }
        }, 5, 5); // 5 ticks = 0.25 seconds

        // Launch fireworks around the player continuously for 30 seconds
        BukkitTask fireworkTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            Location fireworkLocation = player.getLocation().add(Math.random() * 4 - 2, Math.random() * 4,
                    Math.random() * 4 - 2);
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

        // Schedule the removal of all entities and stopping the fireworks after 30
        // seconds
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for (Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof Sheep || entity instanceof Firework) {
                    entity.remove();
                }
            }
            sheepTask.cancel();
            fireworkTask.cancel();
        }, 20 * 30); // 20 ticks per second * 30 seconds
    }
}
