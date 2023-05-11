package com.fabledclan.CustomBlocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WandCrafter extends CustomBlock {
    public WandCrafter(Plugin plugin) {
        super(plugin, "wand_crafter");
    }

    public ItemStack item() {
        ItemStack wandCrafter = new ItemStack(Material.SMITHING_TABLE, 1);
        ItemMeta meta = wandCrafter.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Wand Crafter");
        wandCrafter.setItemMeta(meta);
        return wandCrafter;
    }

    public Recipe recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), "wand_crafter"), getItem());
        recipe.shape(" D ", "DCD", " D ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        return recipe;
    }

    public void placeEvent(BlockPlaceEvent event) {

    }

    public void breakEvent(BlockBreakEvent event) {

    }
    
}
