package com.fabledclan.recipes;

import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public abstract class Recipe {
    private ShapedRecipe shapedRecipe = null;
    private ShapelessRecipe shapelessRecipe = null;

    public Object getRecipe() {
        if (shapedRecipe != null) {
            return shapedRecipe;
        } else if (shapelessRecipe != null) {
            return shapelessRecipe;
        } else {
            System.out.println("Recipe Failure!");
            return false;
        }
    }

    public void setShapedRecipe(ShapedRecipe recipe) {
        shapedRecipe = recipe;
    }

    public void setShapelessRecipe(ShapelessRecipe recipe) {
        shapelessRecipe = recipe;
    }
}
