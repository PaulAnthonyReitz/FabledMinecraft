package com.fabledclan;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.fabledclan.CustomBlocks.CustomBlock;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        for (CustomBlock block : CustomBlockRegistry.getBlocks()) {
            if (block.getItem() == itemInHand) {
                block.placeEvent(event);
            }
        }
    }
}
