package com.fabledclan.CustomItems;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

// CustomItem class is an abstract class that defines all items added to the plugin. The boolean USED_IN_DEFAULT_RECIPES
// determines whether or not the item is allowed to be used in vanilla recipes, i.e. if the Material is a stick it shouldn't
// be crafted into a pickaxe

// item() and recipe() are required to be filled out

public abstract class CustomItem {
    private final String NAME;
    private final Recipe RECIPE;
    private final ItemStack ITEM;
    private final Material MATERIAL;
    private final String DISPLAY_NAME;
    private ArrayList<String> LORE = null;
    private final Boolean USED_IN_DEFAULT_RECIPES;

    public CustomItem(String name, Material material, String displayName, Boolean useInDefaultRecipes) {
        this.NAME = name;
        this.MATERIAL = material;
        this.DISPLAY_NAME = displayName;
        this.ITEM = item();
        this.USED_IN_DEFAULT_RECIPES = useInDefaultRecipes;
        setDefaultRecipes();
        this.RECIPE = recipe();
    }

    public CustomItem(String name, Material material, String displayName, Boolean useInDefaultRecipes, ArrayList<String> lore) {
        this.NAME = name;
        this.MATERIAL = material;
        this.DISPLAY_NAME = displayName;
        this.LORE = lore;
        this.ITEM = item();
        this.USED_IN_DEFAULT_RECIPES = useInDefaultRecipes;
        setDefaultRecipes();
        this.RECIPE = recipe();
    }

    public abstract Recipe recipe();

    public ItemStack item() {
        ItemStack item = new ItemStack(MATERIAL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(DISPLAY_NAME);
        if (LORE != null) {
            meta.setLore(LORE);
        }
        item.setItemMeta(meta);
        return item;
    }

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

    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public Material getMaterial() {
        return MATERIAL;
    }

    public ArrayList<String> getLore() {
        return LORE;
    }

    public Boolean isUsedInDefaultRecipes() {
        return USED_IN_DEFAULT_RECIPES;
    }
}
