package com.fabledclan.CustomItems;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CustomItem {
    private final String NAME;
    private final Recipe RECIPE;
    private final ItemStack ITEM;
    private final Boolean USED_IN_DEFAULT_RECIPES;

    public CustomItem(String name, Boolean useInDefaultRecipes) {
        this.NAME = name;
        this.ITEM = item();
        this.USED_IN_DEFAULT_RECIPES = useInDefaultRecipes;
        setDefaultRecipes();
        this.RECIPE = recipe();
    }

    public abstract Recipe recipe();

    public abstract ItemStack item();

    public void setDefaultRecipes() {
        if (!USED_IN_DEFAULT_RECIPES) {
            ItemMeta meta = getItem().getItemMeta();
            meta.setCustomModelData(12358);
            getItem().setItemMeta(meta);
        }
    }

    public Recipe getRecipe() {
        return RECIPE;
    }

    public ItemStack getItem() {
        return ITEM;
    }

    public String getName() {
        return NAME;
    }

    public Boolean isUsedInDefaultRecipes() {
        return USED_IN_DEFAULT_RECIPES;
    }
}
