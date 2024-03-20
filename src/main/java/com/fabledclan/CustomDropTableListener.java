package com.fabledclan;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CustomDropTableListener {
    private static final Random RANDOM = new Random();
    private static final double DIAMOND_DROP_CHANCE = 0.01; // 1% chance
    private static final double IRON_DROP_CHANCE = 0.04; // 3% chance
    private static final double WOOD_DROP_CHANCE = 0.09; // 5% chance

    private static final List<Material> WOOD_EQUIPMENT = Arrays.asList(
            Material.WOODEN_SWORD,
            Material.WOODEN_PICKAXE,
            Material.WOODEN_AXE,
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.WOODEN_HOE
    );

    private static final List<Material> IRON_EQUIPMENT = Arrays.asList(
            Material.IRON_SWORD,
            Material.IRON_PICKAXE,
            Material.IRON_AXE,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.IRON_HOE
    );

    private static final List<Material> DIAMOND_EQUIPMENT = Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_HOE
    );

    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // Handle null and non-Monster entities
        if (entity == null || !(entity instanceof Monster)) {
            return;
        }

        double roll = RANDOM.nextDouble();
        ItemStack drop = null;

        if (roll < DIAMOND_DROP_CHANCE) {
            drop = getRandomEquipment(DIAMOND_EQUIPMENT);
        } else if (roll < IRON_DROP_CHANCE) {
            drop = getRandomEquipment(IRON_EQUIPMENT);
        } else if (roll < WOOD_DROP_CHANCE) {
            drop = getRandomEquipment(WOOD_EQUIPMENT);
        }

        // Handle null or unmodifiable drop list
        if (drop != null && event.getDrops() != null && event.getDrops().addAll(List.of(drop))) {
            // Drop added successfully
        }
    }

    private static ItemStack getRandomEquipment(List<Material> equipmentList) {
        return new ItemStack(equipmentList.get(RANDOM.nextInt(equipmentList.size())));
    }
}