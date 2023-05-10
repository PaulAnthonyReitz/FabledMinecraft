package com.fabledclan.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import com.fabledclan.Abilities;
import com.fabledclan.SpellAbility;

public class UndeadArmy extends SpellAbility {
    public UndeadArmy(Plugin plugin, Abilities abilities, String name, int requiredMagicLevel, int manaCost) {
        super(plugin, abilities, name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player))
            return;
        // Get the targeted entity
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
                player.getLocation().getDirection(), 100);
        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
            player.sendMessage(ChatColor.RED + "No target found for Undead Army!");
            return;
        }
        LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();

        // Play spooky wolf sound to all players in the area
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL,
                SoundCategory.HOSTILE, 1.0F, 0.5F);

        // Spawn wolves and set their target
        for (int i = 0; i < 8; i++) {
            Location spawnLocation = player.getLocation().add(0, 1, 0);
            Wolf wolf = (Wolf) player.getWorld().spawnEntity(spawnLocation,
                    EntityType.WOLF);
            wolf.setAngry(true); // Wolves are aggressive by default
            wolf.setTarget(target);

            // Schedule the wolf to be removed after 20 seconds
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (!wolf.isDead()) {
                    wolf.remove();
                }
            }, 20 * 20);
        }
    }
}