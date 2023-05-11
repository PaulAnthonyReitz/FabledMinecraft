package com.fabledclan;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import com.fabledclan.CustomBlocks.CustomBlock;

// This class simply initializes all the recipes
//
// Initialized in Main

public class CustomRecipes {
    public static void addRecipes() {
        for (CustomBlock block : CustomBlockRegistry.getBlocks()) {
            Recipe recipe = block.getRecipe();
            if (recipe == null) continue;
            Bukkit.addRecipe(block.getRecipe());
        }
    }
}
