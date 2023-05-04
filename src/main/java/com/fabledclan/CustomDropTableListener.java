package com.fabledclan;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

public class CustomDropTableListener{

    private final Random random = new Random();

    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Monster)) {
            return;
        }
        double roll = random.nextDouble();
        ItemStack drop = null;

        if (roll < 0.01) { // 1% chance for diamond
            drop = getRandomDiamondEquipment();
        } else if (roll < 0.04) { // 3% chance for iron
            drop = getRandomIronEquipment();
        } else if (roll < 0.09) { // 5% chance for wood
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
        return new ItemStack(diamondEquipment[random.nextInt(diamondEquipment.length)]);
    }
}
