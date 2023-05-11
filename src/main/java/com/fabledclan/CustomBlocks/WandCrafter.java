package com.fabledclan.CustomBlocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WandCrafter extends CustomBlock {
    public WandCrafter() {
        super("wand_crafter");
    }

    public ItemStack item() {
        ItemStack wandCrafter = new ItemStack(Material.SMITHING_TABLE, 1);
        ItemMeta meta = wandCrafter.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(getPlugin(), getKey()), PersistentDataType.STRING, getName());
        meta.setDisplayName(ChatColor.RESET + "Wand Crafter");
        wandCrafter.setItemMeta(meta);
        return wandCrafter;
    }

    public Recipe recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), getName()), getItem());
        recipe.shape(" D ", "DCD", " D ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        return recipe;
    }

    public void placeEvent(BlockPlaceEvent event) {
        System.out.println("Wand Crafter Placed!");
        event.setCancelled(true);
    }

    public void breakEvent(BlockBreakEvent event) {

    }
    
}
