package com.fabledclan.recipes;

import org.bukkit.inventory.Recipe;

public abstract class RecipeClass {
    private Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
