package com.fabledclan.Listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.DatabaseManager;
import com.fabledclan.EnemyData;
import com.fabledclan.Main;

public class EntityDamageListenerMobs implements Listener {

    private final Map<String, Integer> mobDefenseCache = new HashMap<>();

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Creature)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        Creature creature = (Creature) event.getEntity();

        // Get the player's attack damage from the database
        int playerAttack = DatabaseManager.getPlayerStats(player.getUniqueId()).getAttack();

        // Get the mob level
        PersistentDataContainer dataContainer = creature.getPersistentDataContainer();
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);

        // Get the mob defense value
        int mobDefense = getMobEnemyDefense(creature);

        // Calculate the final defense value
        int mobScaledDefense = mobDefense + (enemyLevel * mobDefense);

        // Calculate the final damage after accounting for player attack and mob defense
        int finalDamage = Math.max(1, ((int)Math.round(event.getDamage())) + playerAttack - mobScaledDefense);

        // Apply the final damage to the mob
        event.setDamage(finalDamage);

        double maxHealth = creature.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        int roundedHealth = (int) Math.round(creature.getHealth() - finalDamage);
        roundedHealth = Math.max(0, roundedHealth); // Ensure health doesn't go below 0
        String entityType = creature.getType().toString();
        double healthPercentage = (double) roundedHealth / maxHealth;

        String heartColor;
        if (healthPercentage > 0.5) {
            heartColor = "\u00A75"; // Purple
        } else if (healthPercentage > 0.15) {
            heartColor = "\u00A76"; // Yellow
        } else {
            heartColor = "\u00A74"; // Red
        }

        // Check if the mob is a NM
        NamespacedKey nmKey = new NamespacedKey(Main.getPlugin(), "nm");
        boolean isNM = dataContainer.getOrDefault(nmKey, PersistentDataType.INTEGER, 0) == 1;

        // If the mob is a NM, use a different string for the custom name
        String healthInfo;
        if (isNM) {
            // Use the custom name of the NM, you should replace `getNMCustomName` with the appropriate method to get the custom name
            NamespacedKey nmNameKey = new NamespacedKey(Main.getPlugin(), "NMName");
            String NMNAME = dataContainer.getOrDefault(nmNameKey, PersistentDataType.STRING, "SILLY GOOSE");
            healthInfo = NMNAME + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764";
        } else {
            healthInfo = "[\u00A76" + enemyLevel + "\u00A7f] " + entityType + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764";
        }


        creature.setCustomName(healthInfo);
        creature.setCustomNameVisible(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (creature.isDead()) {
                    return;
                }

                creature.setCustomNameVisible(false);
            }
        }.runTaskLater(Main.getPlugin(), 5 * 20); // 5 seconds * 20 ticks per second

            }

    private int getMobEnemyDefense(LivingEntity entity) {
        String entityType = entity.getType().name();

        // Check if the defense value is in the cache
        if (!mobDefenseCache.containsKey(entityType)) {
            // Get the enemy data from the database
            EnemyData enemyData = DatabaseManager.getEnemyData(entityType);

            // Get the defense value from the enemy data
            int defense = enemyData != null ? enemyData.defense : 0;

            // Store the defense value in the cache
            mobDefenseCache.put(entityType, defense);
        }

        return mobDefenseCache.get(entityType);
    }
}
