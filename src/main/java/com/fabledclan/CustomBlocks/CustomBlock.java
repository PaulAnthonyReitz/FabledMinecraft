package com.fabledclan.CustomBlocks;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

public abstract class CustomBlock {
    private final String name;
    private final Recipe recipe;
    private final Plugin plugin;
    private final ItemStack item;

    public CustomBlock(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.item = item();
        this.recipe = recipe(this.item);
    }

    public abstract ItemStack item();

    public abstract Recipe recipe(ItemStack item);

    public abstract void placeEvent(BlockPlaceEvent event);

    public abstract void breakEvent(BlockBreakEvent event);

    public String getName() {
        return name;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ItemStack getItem() {
        return item;
    }
}
