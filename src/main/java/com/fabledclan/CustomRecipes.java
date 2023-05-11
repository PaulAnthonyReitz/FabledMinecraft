package com.fabledclan;

import org.bukkit.Bukkit;

import com.fabledclan.CustomBlocks.CustomBlock;

public class CustomRecipes {
    public static void addRecipes() {
        for (CustomBlock block : CustomBlockRegistry.getBlocks()) {
            Bukkit.addRecipe(block.getRecipe());
        }
    }
}
