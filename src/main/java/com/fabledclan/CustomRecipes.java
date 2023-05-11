package com.fabledclan;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import com.fabledclan.recipes.FakeDiamond;

public class CustomRecipes {
    private ArrayList<Recipe> recipes;
    private Plugin plugin;

    public CustomRecipes(Plugin plugin) {
        this.plugin = plugin;
        addRecipes();
    }

    private void initRecipes() {
        this.recipes = new ArrayList<Recipe>(
            Arrays.asList(
                // Register all recipes here
                new FakeDiamond(plugin).getRecipe()
            )
        );
    }

    private void addRecipes() {
        initRecipes();
        for (Recipe recipe : this.recipes) {
            Bukkit.addRecipe(recipe);
        }
    }
}
