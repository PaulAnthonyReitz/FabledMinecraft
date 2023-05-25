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

    int distancePerLevel = 250;

    public void setEnemyLevel(Entity entity) {
        // Calculate the enemy level
        Location spawnLocation = entity.getWorld().getSpawnLocation();
        Location enemyLocation = entity.getLocation();
    
        double distance = spawnLocation.distance(enemyLocation);
        int enemyLevel = (int) (distance / distancePerLevel) + 1;
    
        // Get the enemy's data container
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
    
        // Create the namespaced key for the level
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
    
        // Set the level in the data container
        dataContainer.set(levelKey, PersistentDataType.INTEGER, enemyLevel);
    }
    

    public void setEnemyMaxHP(Entity entity) {
        // Set the enemy level
        setEnemyLevel(entity);
    
        // Get the enemy's data container
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
    
        // Create the namespaced key for the level
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
    
        // Get the enemy level from the data container
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);
    
        // Get the enemy data from the cache
        EnemyData enemyData = Main.getCachedEnemyData(entity.getType());
    
        if (enemyData != null) {
            // Calculate the max HP based on the level and HP scale
            int maxHP = enemyData.hp + (enemyLevel - 1) * enemyData.hpScale;
    
            // Set the max HP of the entity
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                livingEntity.setHealth(maxHP);
            }
        }
    }
    
    
    
    
}
