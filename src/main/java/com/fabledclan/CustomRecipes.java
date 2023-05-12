package com.fabledclan;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import com.fabledclan.CustomBlocks.CustomBlock;
import com.fabledclan.CustomItems.CustomItem;

// This class simply initializes all the recipes
//
// Initialized in Main

public class CustomRecipes {
    public static void addRecipes() {
        for (CustomBlock block : CustomBlockRegistry.getBlocks()) {
            Recipe recipe = block.getRecipe();
            if (recipe == null) continue;
            Bukkit.addRecipe(recipe);
        }
        for (CustomItem item : CustomItemRegistry.getItems()) {
            Recipe recipe = item.getRecipe();
            if (recipe == null) continue;
            Bukkit.addRecipe(recipe);
        }
    }
}
