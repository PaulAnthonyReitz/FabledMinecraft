package com.fabledclan.CustomItems;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class CustomItem {
    private final String NAME;
    private final Recipe RECIPE;
    private final ItemStack ITEM;

    public CustomItem(String name) {
        this.NAME = name;
        this.ITEM = item();
        this.RECIPE = recipe();
    }

    public abstract Recipe recipe();

    public abstract ItemStack item();

    public Recipe getRecipe() {
        return RECIPE;
    }

    public ItemStack getItem() {
        return ITEM;
    }

    public String getName() {
        return NAME;
    }
}
