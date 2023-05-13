package com.fabledclan.CustomBlocks;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;
import com.fabledclan.CustomItems.CustomItem;

// This abstract class is the base for all custom blocks in the plugin
// The only thing it needs passed into the super() is the name of it

// It is required when making a new block to initialize the abstract methods: item(), recipe(), placeEvent(), breakEvent()
// if the block doesn't have a recipe you can set the method to return 'null' (the recipe handler will deal with this)
// also the event methods don't have to be filled out but it may be requried to initialize them

public abstract class CustomBlock extends CustomItem {
    private static final String KEY = "custom_block"; // key used for data containers

    public CustomBlock(String name, String displayName, Material material) {
        super(name, material, displayName, false);
    }

    public CustomBlock(String name, String displayName, Material material, ArrayList<String> lore) {
        super(name, material, displayName, false, lore);
    }

    public ItemStack item() {
        ItemStack item = new ItemStack(getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getDisplayName());
        if (getLore() != null) {
            meta.setLore(getLore());
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(Main.getPlugin(), KEY), PersistentDataType.STRING, getName());
        item.setItemMeta(meta);
        return item;
    }

    public abstract Recipe recipe();

    public abstract void placeEvent(BlockPlaceEvent event);

    public abstract void breakEvent(BlockBreakEvent event);

    public static String getKey() {
        return KEY;
    }
}
