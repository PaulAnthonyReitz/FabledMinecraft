package com.fabledclan.CustomBlocks;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.fabledclan.CustomItems.CustomItem;

// This abstract class is the base for all custom blocks in the plugin
// The only thing it needs passed into the super() is the name of it

// It is required when making a new block to initialize the abstract methods: item(), recipe(), placeEvent(), breakEvent()
// if the block doesn't have a recipe you can set the method to return 'null' (the recipe handler will deal with this)
// also the event methods don't have to be filled out but it may be requried to initialize them

public abstract class CustomBlock extends CustomItem {
    private final Material material;
    private static final String KEY = "custom_block"; // key used for data containers

    public CustomBlock(String name, Material material) {
        super(name, false);
        this.material = material;
    }

    public abstract ItemStack item();

    public abstract Recipe recipe();

    public abstract void placeEvent(BlockPlaceEvent event);

    public abstract void breakEvent(BlockBreakEvent event);

    public Material getMaterial() {
        return material;
    }

    public static String getKey() {
        return KEY;
    }
}
