package com.fabledclan;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.fabledclan.CustomBlocks.CustomBlock;

// Listens for any custom block that was placed and calls the placeEvent() method

public class BlockPlaceListener implements Listener {
    private final Plugin PLUGIN;

    public BlockPlaceListener(Plugin plugin) {
        this.PLUGIN = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer data = event.getItemInHand().getItemMeta().getPersistentDataContainer(); // gets the data container of the item
        NamespacedKey key = new NamespacedKey(PLUGIN, CustomBlock.getKey()); // creates the key used for the block
        if (!data.has(key, PersistentDataType.STRING)) return; // checks if the block has a value for the key
        for (CustomBlock block : CustomBlockRegistry.getBlocks()) { // loops through all the custom blocks
            String block_key = data.get(key, PersistentDataType.STRING); // gets the value of the block from the key
            if (block.getName().equals(block_key)) block.placeEvent(event); // executes placeEvent() method on the block that matches
        }
    }
}
