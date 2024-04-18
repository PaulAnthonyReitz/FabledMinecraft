package com.fabledclan.Enemy;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;

public class CalculateEnemyLevel {
    private static final NamespacedKey LEVEL_KEY = new NamespacedKey(Main.getPlugin(), "enemy_level");
    private final int distancePerLevel = 250;

    private int calculateEnemyLevel(Location enemyLocation) {
        Location spawnLocation = enemyLocation.getWorld().getSpawnLocation();
        double distance = spawnLocation.distance(enemyLocation);
        return (int) (distance / distancePerLevel) + 1;
    }

    public void setEnemyLevel(Entity entity) {
        if (entity != null) {
            int enemyLevel = calculateEnemyLevel(entity.getLocation());
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            dataContainer.set(LEVEL_KEY, PersistentDataType.INTEGER, enemyLevel);
        }
    }

    public void setEnemyMaxHP(Entity entity) {
        if (entity != null) {
            setEnemyLevel(entity);
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            int enemyLevel = dataContainer.getOrDefault(LEVEL_KEY, PersistentDataType.INTEGER, 1);
            EnemyData enemyData = Main.getCachedEnemyData(entity.getType());

            if (enemyData != null) {
                int maxHP = enemyData.hp + (enemyLevel - 1) * enemyData.hpScale;
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity != null) {
                        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                        livingEntity.setHealth(maxHP);
                    }
                }
            } else {
                Main.getPlugin().getLogger().warning("Enemy data not found for entity type: " + entity.getType());
            }
        }
    }
}