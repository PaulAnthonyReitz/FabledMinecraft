package com.fabledclan.Listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
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
import com.fabledclan.Main;
import com.fabledclan.Enemy.EnemyData;
import com.fabledclan.Player.PlayerStats;

public class EntityDeathListener implements Listener {
    private static final NamespacedKey LEVEL_KEY = new NamespacedKey(Main.getPlugin(), "enemy_level");
    private final Map<String, EnemyData> enemyDataCache = new HashMap<>();

    private enum EquipmentType {
        WEAPON,
        ARMOR
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getEntity().getKiller();

        if (player != null) {
            String entityType = entity.getType().name();

            if (!enemyDataCache.containsKey(entityType)) {
                EnemyData enemyData = DatabaseManager.getEnemyData(entityType);
                enemyDataCache.put(entityType, enemyData);
            }

            EnemyData cachedEnemyData = enemyDataCache.get(entityType);

            if (cachedEnemyData != null) {
                int baseExp = cachedEnemyData.baseExp;
                int expScale = cachedEnemyData.expScale;
                int enemyLevel = getEnemyLevel(entity);
                int expToGrant = baseExp + (expScale * (enemyLevel - 1));

                PlayerStats stats = Main.getPlayerStatsCache().get(player.getUniqueId());
                if (stats != null) {
                    stats.setExp(stats.getExp() + expToGrant);
                }

                player.sendMessage(ChatColor.GREEN + "You gained " + expToGrant + " exp.");
            }
        }

        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            setCustomDrops(event);
            int monsterLevel = getEnemyLevel(entity);

            for (ItemStack item : event.getDrops()) {
                EquipmentType equipmentType = getEquipmentType(item.getType());

                if (equipmentType == EquipmentType.WEAPON) {
                    ItemMeta itemMeta = item.getItemMeta();
                    double baseAttackValue = getBaseAttackValue(item.getType());
                    itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", baseAttackValue + monsterLevel - 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                    item.setItemMeta(itemMeta);
                } else if (equipmentType == EquipmentType.ARMOR) {
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

    private int getEnemyLevel(Entity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        return dataContainer.getOrDefault(LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }

    private EquipmentType getEquipmentType(Material material) {
        if (material.toString().endsWith("_SWORD") || material.toString().endsWith("_AXE") || material.toString().endsWith("_PICKAXE") || material == Material.BOW || material == Material.CROSSBOW || material == Material.TRIDENT) {
            return EquipmentType.WEAPON;
        } else if (material.toString().endsWith("_HELMET") || material.toString().endsWith("_CHESTPLATE") || material.toString().endsWith("_LEGGINGS") || material.toString().endsWith("_BOOTS")) {
            return EquipmentType.ARMOR;
        }
        return null;
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

    private void setCustomDrops(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Monster)) {
            return;
        }

        Random random = new Random();
        int roll = random.nextInt(100) + 1;
        ItemStack drop = null;

        if (roll < 3) {
            drop = getRandomEquipment(getDiamondEquipmentTypes());
        } else if (roll < 6) {
            drop = getRandomEquipment(getIronEquipmentTypes());
        } else if (roll < 8) {
            drop = getRandomEquipment(getWoodEquipmentTypes());
        }

        if (drop != null) {
            event.getDrops().add(drop);
        }
    }

    private ItemStack getRandomEquipment(Material[] equipmentTypes) {
        Random random = new Random();
        return new ItemStack(equipmentTypes[random.nextInt(equipmentTypes.length)]);
    }

    private Material[] getWoodEquipmentTypes() {
        return new Material[]{
                Material.WOODEN_SWORD,
                Material.WOODEN_PICKAXE,
                Material.WOODEN_AXE,
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        };
    }

    private Material[] getIronEquipmentTypes() {
        return new Material[]{
                Material.IRON_SWORD,
                Material.IRON_PICKAXE,
                Material.IRON_AXE,
                Material.IRON_HELMET,
                Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS,
                Material.IRON_BOOTS
        };
    }

    private Material[] getDiamondEquipmentTypes() {
        return new Material[]{
                Material.DIAMOND_SWORD,
                Material.DIAMOND_PICKAXE,
                Material.DIAMOND_AXE,
                Material.DIAMOND_HELMET,
                Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_BOOTS
        };
    }
}