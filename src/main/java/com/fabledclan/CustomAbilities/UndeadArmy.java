package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.RayTraceResult;

import com.fabledclan.Main;

public class UndeadArmy extends SpellAbility {
    private static final int ARMY_SIZE = 6;
    private static final int ARMY_DURATION = 20 * 20; // 20 seconds in ticks
    private static final String ARMY_METADATA_KEY = "UndeadArmy";

    public UndeadArmy() {
        super("undead_army", 1, 75);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) {
            return;
        }

        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
                player.getLocation().getDirection(), 100, entity -> !entity.getUniqueId().equals(player.getUniqueId()));

        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
            player.sendMessage(ChatColor.RED + "No target found for Undead Army!");
            return;
        }

        LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
        playSpawnSound(player.getLocation());
        spawnUndeadArmy(player, target);
    }

    private void playSpawnSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 0.5F);
    }

    private void spawnUndeadArmy(Player player, LivingEntity target) {
        for (int i = 0; i < ARMY_SIZE; i++) {
            Location spawnLocation = player.getLocation().add(0, 1, 0);
            Zombie zombie = (Zombie) player.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
            zombie.setTarget(target);
            zombie.setRemoveWhenFarAway(false);
            zombie.setMetadata(ARMY_METADATA_KEY, new FixedMetadataValue(Main.getPlugin(), true));
            makeFriendly(player, zombie);
            scheduleRemoval(zombie);
        }
    }

    private void makeFriendly(Player player, Zombie zombie) {
        zombie.setRemoveWhenFarAway(false);
        zombie.setTarget(null); // Clear the target to prevent attacking the player
        zombie.setMaxHealth(20); // Adjust health as needed
        zombie.setHealth(zombie.getMaxHealth());
    }

    private void scheduleRemoval(Zombie zombie) {
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            if (!zombie.isDead() && zombie.hasMetadata(ARMY_METADATA_KEY)) {
                zombie.remove();
            }
        }, ARMY_DURATION);
    }
}