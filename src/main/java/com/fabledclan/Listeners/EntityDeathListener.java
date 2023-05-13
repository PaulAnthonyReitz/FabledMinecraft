package com.fabledclan.Listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.DatabaseManager;
import com.fabledclan.EnemyData;
import com.fabledclan.Main;

import java.util.Random;

public class EntityDeathListener implements Listener {
    private final Map<String, EnemyData> enemyDataCache = new HashMap<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getEntity().getKiller();
    
        if (player != null) {
            String entityType = entity.getType().name();
    
            // Check if the enemy data is in the cache
            if (!enemyDataCache.containsKey(entityType)) {
                // Fetch enemy data from the database and store it in the cache
                EnemyData enemyData = DatabaseManager.getEnemyData(entityType);
                enemyDataCache.put(entityType, enemyData);
            }
    
            EnemyData cachedEnemyData = enemyDataCache.get(entityType);
    
            if (cachedEnemyData != null) {
                int baseExp = cachedEnemyData.baseExp;
                float expScale = cachedEnemyData.expScale;
                int enemyLevel = Math.max(1, getEnemyLevel(entity));
                int expToGrant = (int) (baseExp * Math.pow(expScale, enemyLevel - 1));
                DatabaseManager.addExp(player.getUniqueId(), expToGrant);
                player.sendMessage(ChatColor.GREEN + "You gained " + expToGrant + " exp.");
            }
        }
    
        // Check if the entity is a LivingEntity and a monster
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            //shouldnt happen on player deaths
            setCustomDrops(event);
            int monsterLevel = Math.max(1, getEnemyLevel(entity));
    
            // Loop through the dropped items
            for (ItemStack item : event.getDrops()) {
                // Check if the item is a weapon
                if (isWeapon(item.getType())) {
                 // Modify the weapon's attack damage based on the monster's level and the base attack value
                 ItemMeta itemMeta = item.getItemMeta();
                 double baseAttackValue = getBaseAttackValue(item.getType());
                 itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", baseAttackValue + monsterLevel - 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                 item.setItemMeta(itemMeta);
                } else if (isArmor(item.getType())) {
                    // Modify the armor's defense value based on the monster's level
                    ItemMeta itemMeta = item.getItemMeta();
                    double baseDefenseValue = getBaseDefenseValue(item.getType());
                    double armorStatBonus = monsterLevel;
                    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", baseDefenseValue + armorStatBonus, AttributeModifier.Operation.ADD_NUMBER, getEquipmentSlot(item.getType()));
                    itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);
                    item.setItemMeta(itemMeta);
                }
            }
        }
    }

    private double getBaseAttackValue(Material material) {
        Map<Material, Double> baseAttackValues = new HashMap<>();
        baseAttackValues.put(Material.WOODEN_SWORD, 4.0);
        baseAttackValues.put(Material.STONE_SWORD, 5.0);
        baseAttackValues.put(Material.IRON_SWORD, 6.0);
        baseAttackValues.put(Material.GOLDEN_SWORD, 4.0);
        baseAttackValues.put(Material.DIAMOND_SWORD, 7.0);
        baseAttackValues.put(Material.NETHERITE_SWORD, 8.0);
        // Add other weapons and their base attack values as needed

        return baseAttackValues.getOrDefault(material, 0.0);
    }

    private double getBaseDefenseValue(Material material) {
        Map<Material, Double> baseDefenseValues = new HashMap<>();
        baseDefenseValues.put(Material.LEATHER_HELMET, 1.0);
        baseDefenseValues.put(Material.LEATHER_CHESTPLATE, 3.0);
        baseDefenseValues.put(Material.LEATHER_LEGGINGS, 2.0);
        baseDefenseValues.put(Material.LEATHER_BOOTS, 1.0);
    
        baseDefenseValues.put(Material.CHAINMAIL_HELMET, 2.0);
        baseDefenseValues.put(Material.CHAINMAIL_CHESTPLATE, 5.0);
        baseDefenseValues.put(Material.CHAINMAIL_LEGGINGS, 4.0);
        baseDefenseValues.put(Material.CHAINMAIL_BOOTS, 1.0);
    
        baseDefenseValues.put(Material.IRON_HELMET, 2.0);
        baseDefenseValues.put(Material.IRON_CHESTPLATE, 6.0);
        baseDefenseValues.put(Material.IRON_LEGGINGS, 5.0);
        baseDefenseValues.put(Material.IRON_BOOTS, 2.0);
    
        baseDefenseValues.put(Material.GOLDEN_HELMET, 2.0);
        baseDefenseValues.put(Material.GOLDEN_CHESTPLATE, 5.0);
        baseDefenseValues.put(Material.GOLDEN_LEGGINGS, 3.0);
        baseDefenseValues.put(Material.GOLDEN_BOOTS, 1.0);
    
        baseDefenseValues.put(Material.DIAMOND_HELMET, 3.0);
        baseDefenseValues.put(Material.DIAMOND_CHESTPLATE, 8.0);
        baseDefenseValues.put(Material.DIAMOND_LEGGINGS, 6.0);
        baseDefenseValues.put(Material.DIAMOND_BOOTS, 3.0);
    
        baseDefenseValues.put(Material.NETHERITE_HELMET, 3.0);
        baseDefenseValues.put(Material.NETHERITE_CHESTPLATE, 8.0);
        baseDefenseValues.put(Material.NETHERITE_LEGGINGS, 6.0);
        baseDefenseValues.put(Material.NETHERITE_BOOTS, 3.0);
    
        // Add other armors and their base defense values as needed
    
        return baseDefenseValues.getOrDefault(material, 0.0);
    }
    
    

    private EquipmentSlot getEquipmentSlot(Material material) {
        switch (material) {
            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
                return EquipmentSlot.FEET;
            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
                return EquipmentSlot.CHEST;
            case LEATHER_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
                return EquipmentSlot.HEAD;
            default:
                return null;
        }
    }
    
    
    private boolean isWeapon(Material material) {
        Set<Material> weapons = new HashSet<>(Arrays.asList(
                Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
                Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                Material.BOW, Material.CROSSBOW, Material.TRIDENT
        ));
        return weapons.contains(material);
    }
    

    private boolean isArmor(Material material) {
        Set<Material> armors = new HashSet<>(Arrays.asList(
                Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
                Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
                Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
                Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS
        ));
        return armors.contains(material);
    }
    
    
    

    private int getEnemyLevel(Entity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);
        return enemyLevel;
    }
    

        public static int generateEnemyLevel(Entity entity) {
            Location worldSpawn = entity.getWorld().getSpawnLocation();
            Location enemySpawn = entity.getLocation();
            double distance = worldSpawn.distance(enemySpawn);
        
            // Calculate enemy level based on distance from world spawn
            int enemyLevel = (int) Math.floor(distance / 1000);
        
            return enemyLevel;
        }

        private void setCustomDrops(EntityDeathEvent event) {
            LivingEntity entity = event.getEntity();
    
            if (!(entity instanceof Monster)) {
                return;
            }
            Random random = new Random();
            int roll = random.nextInt(100) + 1;
            ItemStack drop = null;
    
            if (roll < 3) { // 3% chance for diamond
                drop = getRandomDiamondEquipment();
            } else if (roll < 6) { // 5% chance for iron
                drop = getRandomIronEquipment();
            } else if (roll < 8) { // 8% chance for wood
                drop = getRandomWoodEquipment();
            }
    
            if (drop != null) {
                event.getDrops().add(drop);
            }
        }
    
        private ItemStack getRandomWoodEquipment() {
            Material[] woodEquipment = {
                    Material.WOODEN_SWORD,
                    Material.WOODEN_PICKAXE,
                    Material.WOODEN_AXE,
                    Material.LEATHER_HELMET,
                    Material.LEATHER_CHESTPLATE,
                    Material.LEATHER_LEGGINGS,
                    Material.LEATHER_BOOTS
            };
            Random random = new Random();
            return new ItemStack(woodEquipment[random.nextInt(woodEquipment.length)]);
        }
    
        private ItemStack getRandomIronEquipment() {
            Material[] ironEquipment = {
                    Material.IRON_SWORD,
                    Material.IRON_PICKAXE,
                    Material.IRON_AXE,
                    Material.IRON_HELMET,
                    Material.IRON_CHESTPLATE,
                    Material.IRON_LEGGINGS,
                    Material.IRON_BOOTS
            };
            Random random = new Random();
            return new ItemStack(ironEquipment[random.nextInt(ironEquipment.length)]);
        }
    
        private ItemStack getRandomDiamondEquipment() {
            Material[] diamondEquipment = {
                    Material.DIAMOND_SWORD,
                    Material.DIAMOND_PICKAXE,
                    Material.DIAMOND_AXE,
                    Material.DIAMOND_HELMET,
                    Material.DIAMOND_CHESTPLATE,
                    Material.DIAMOND_LEGGINGS,
                    Material.DIAMOND_BOOTS
            };
            Random random = new Random();
            return new ItemStack(diamondEquipment[random.nextInt(diamondEquipment.length)]);
        }
    }

