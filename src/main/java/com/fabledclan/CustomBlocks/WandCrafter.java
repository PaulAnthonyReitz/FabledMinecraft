package com.fabledclan.CustomBlocks;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandCrafter extends CustomContainer {

    public WandCrafter() {
        super("wand_crafter", Material.SMITHING_TABLE);
    }

    public ItemStack item() {
        ItemStack wandCrafter = new ItemStack(getMaterial(), 1);
        ItemMeta meta = wandCrafter.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(getPlugin(), getKey()), PersistentDataType.STRING, getName());
        meta.setDisplayName(ChatColor.RESET + "Wand Crafter");
        meta.setLore(new ArrayList<String>(Arrays.asList("Requires the power", "of Crying Obsidian")));
        wandCrafter.setItemMeta(meta);
        return wandCrafter;
    }

    public Recipe recipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), getName()), getItem());
        recipe.shape(" D ", "DCD", " D ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        return recipe;
    }

    public Inventory makeInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        return inventory;
    }

    public void placeEvent(BlockPlaceEvent event) {
        defaultPlace(event);
    }

    public void breakEvent(BlockBreakEvent event) {
        defaultBreak(event);
    }

    public void interactEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().openInventory(getInventory());
    }
    
}
