package com.fabledclan;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntitySpawnListener implements Listener {
    private final Main plugin;
    private final Random random = new Random();

    public EntitySpawnListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
    
        if (entity instanceof Monster) {

            boolean isRare = random.nextDouble() < 0.05;

            LivingEntity livingEntity = (LivingEntity) entity;
            int enemyLevel = generateEnemyLevel(entity);

            if (isRare) {
                // Increase the enemy level by a random number between 1 and 25
                enemyLevel += random.nextInt(25) + 1;

                // Assign a randomly generated name to the rare monster
                String rareName = ChatColor.GOLD + generateRandomName();
                livingEntity.setCustomName(rareName);
                livingEntity.setCustomNameVisible(true);

                // Apply power-ups to the rare monster
                applyPowerUps(livingEntity);
            }
    
            PersistentDataContainer dataContainer = livingEntity.getPersistentDataContainer();
            NamespacedKey levelKey = new NamespacedKey(plugin, "enemy_level");
            dataContainer.set(levelKey, PersistentDataType.INTEGER, enemyLevel);
    
            // Retrieve the enemy data from the database
            EnemyData enemyData = plugin.getCachedEnemyData(entity.getType());
    
            if (enemyData != null) {
                int hpScale = enemyData.hpScale;
                double baseHp = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
                double newHp = baseHp * Math.pow(hpScale, enemyLevel - 1);
    
                // Set the monster's health and max health based on its level
                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHp);
                livingEntity.setHealth(newHp);
            }
        }
    }
    
    
    public int generateEnemyLevel(Entity entity) {
        Location worldSpawn = entity.getWorld().getSpawnLocation();
        Location enemySpawn = entity.getLocation();
        double distance = worldSpawn.distance(enemySpawn);

        // Calculate enemy level based on distance from world spawn
        int enemyLevel = (int) Math.floor(distance / 1000);

        return enemyLevel;
    }

    private String generateRandomName() {
        String[] adjectives = {"Fierce", "Swift", "Infernal", "Shadowy", "Gargantuan"};
        String[] names = {"Dragon", "Goblin", "Wraith", "Behemoth", "Colossus"};

        String adjective = adjectives[random.nextInt(adjectives.length)];
        String name = names[random.nextInt(names.length)];

        return adjective + " " + name;
    }

    private void applyPowerUps(LivingEntity livingEntity) {
        // Example: Give the rare monster a powerful enchanted weapon
        ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        weapon.setItemMeta(weaponMeta);
        livingEntity.getEquipment().setItemInMainHand(weapon);
    }

}
