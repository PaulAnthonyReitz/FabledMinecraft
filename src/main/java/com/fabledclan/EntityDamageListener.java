package com.fabledclan;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityDamageListener implements Listener {

    private final Main plugin;
    private final DatabaseManager databaseManager;
    private final Map<String, Integer> mobDefenseCache;

    public EntityDamageListener(Main plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.mobDefenseCache = new HashMap<>();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Player player = null;
        boolean damageByPlayer = false;
        boolean magicDamage = false;
        int magicLevel = 1;
    
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            damageByPlayer = true;
        }
        else if (event.getDamager() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getDamager();
    
            if (fireball.hasMetadata("magicLevel")) {
                magicLevel = fireball.getMetadata("magicLevel").get(0).asInt();
                magicDamage = true;
                damageByPlayer = true;
            }
        }
        else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }
            player = (Player) arrow.getShooter();
            damageByPlayer = true;
        } 
        else {
            return;
        }
    
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
    
        LivingEntity entity = (LivingEntity) event.getEntity();
    
        if (damageByPlayer && player != null ) {
            if (entity instanceof Player) {
                // Put your logic for when a player damages another player here
                int playerAttack = databaseManager.getPlayerStats(player.getUniqueId()).getAttack();
                int playerDefending = databaseManager.getPlayerStats(entity.getUniqueId()).getDefense();
                int playerLevel = databaseManager.getPlayerStats(entity.getUniqueId()).getLevel();
                int damage;
                if (!magicDamage)
                {   
                    damage = Math.max(1, playerAttack - playerDefending);
                }
                else
                {
                    // Modify the damage based on the magic level
                    damage = (int) Math.max(1, (1 + magicLevel) - playerDefending);
                }
                event.setDamage(damage);

                //HP BARS?
                double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                int roundedHealth = (int) Math.round(entity.getHealth());
                String entityType = entity.getType().toString();
                double healthPercentage = (double) roundedHealth / maxHealth;

                String heartColor;
                if (healthPercentage > 0.5) {
                    heartColor = "\u00A75"; // Purple
                } else if (healthPercentage > 0.15) {
                    heartColor = "\u00A76"; // Yellow
                } else {
                    heartColor = "\u00A74"; // Red
                }

                    String healthInfo = "[\u00A76" + playerLevel + "\u00A7f] " + entityType + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764";


                entity.setCustomName(healthInfo);
                entity.setCustomNameVisible(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (entity.isDead()) {
                            return;
                        }

                        entity.setCustomNameVisible(false);
                    }
                }.runTaskLater(plugin, 5 * 20); // 5 seconds * 20 ticks per second

            } else if (entity instanceof Creature) {
                // Put your logic for when a player damages a creature here
                int playerAttack = databaseManager.getPlayerStats(player.getUniqueId()).getAttack();
                        // Get the mob level
                PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
                NamespacedKey levelKey = new NamespacedKey(plugin, "enemy_level");
                int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);

                // Get the mob defense value
                int mobDefense = getMobEnemyDefense(entity);

                // Calculate the final defense value
                int mobScaledDefense = mobDefense + (enemyLevel * mobDefense);

                // Calculate the final damage after accounting for player attack and mob defense
                int finalDamage;

                if (!magicDamage) {
                    finalDamage = Math.max(1, ((int)Math.round(event.getDamage())) + playerAttack - mobScaledDefense);
                } else {
                    finalDamage = (int) Math.max(1, (1 + magicLevel) - mobScaledDefense); // Adjust the damage multiplier as needed
                }

                // Apply the final damage to the mob
                event.setDamage(finalDamage);

                double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                int roundedHealth = (int) Math.round(entity.getHealth());
                String entityType = entity.getType().toString();
                double healthPercentage = (double) roundedHealth / maxHealth;

                String heartColor;
                if (healthPercentage > 0.5) {
                    heartColor = "\u00A75"; // Purple
                } else if (healthPercentage > 0.15) {
                    heartColor = "\u00A76"; // Yellow
                } else {
                    heartColor = "\u00A74"; // Red
                }

                    String healthInfo = "[\u00A76" + enemyLevel + "\u00A7f] " + entityType + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764";


                entity.setCustomName(healthInfo);
                entity.setCustomNameVisible(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (entity.isDead()) {
                            return;
                        }

                        entity.setCustomNameVisible(false);
                    }
                }.runTaskLater(plugin, 5 * 20); // 5 seconds * 20 ticks per second
            }
        }
    
        // Get the player's attack damage from the database


    }

    private int getMobEnemyDefense(LivingEntity entity) {
        String entityType = entity.getType().name();

        // Check if the defense value is in the cache
        if (!mobDefenseCache.containsKey(entityType)) {
            // Get the enemy data from the database
            EnemyData enemyData = databaseManager.getEnemyData(entityType);

            // Get the defense value from the enemy data
            int defense = enemyData != null ? enemyData.defense : 0;

            // Store the defense value in the cache
            mobDefenseCache.put(entityType, defense);
        }

        return mobDefenseCache.get(entityType);
    }
}
