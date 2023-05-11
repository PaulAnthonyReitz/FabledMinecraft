package com.fabledclan;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import com.fabledclan.recipes.FakeDiamond;

public class CraftingRecipes {
    private ArrayList<ShapedRecipe> shapedRecipes;
    private ArrayList<ShapelessRecipe> shapelessRecipes;
    private Plugin plugin;

    public CraftingRecipes(Plugin plugin) {
        this.plugin = plugin;
        addRecipes();
    }

    private void initRecipes() {
        this.shapedRecipes = new ArrayList<ShapedRecipe>(
            Arrays.asList(
                // Register shaped recipes here:
                (ShapedRecipe) new FakeDiamond(plugin).getRecipe()
                ));
        this.shapelessRecipes = new ArrayList<ShapelessRecipe>(Arrays.asList(
            // Register shapeless recipes here
        ));
    }

    private void addRecipes() {
        initRecipes();
        for (ShapedRecipe recipe : shapedRecipes) {
            Bukkit.addRecipe(recipe);
        }
        for (ShapelessRecipe recipe : shapelessRecipes) {
            Bukkit.addRecipe(recipe);
        }
    }
}
